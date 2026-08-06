package br.edu.infnet.ecommerce.service;

import br.edu.infnet.ecommerce.entity.*;
import br.edu.infnet.ecommerce.exception.EstoqueInsuficienteException;
import br.edu.infnet.ecommerce.exception.PagamentoRecusadoException;
import br.edu.infnet.ecommerce.exception.RecursoNaoEncontradoException;
import br.edu.infnet.ecommerce.repository.*;
import br.edu.infnet.ecommerce.request.CriarPedidoRequest;
import br.edu.infnet.ecommerce.request.ItemPedidoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PedidoService {

    /*
     * Classe central da atividade.
     *
     * Ela acessa diretamente repositórios de Usuário, Produto, Estoque,
     * Pedido e Pagamento, além de chamar PagamentoService.
     * Essa mistura é proposital.
     */
    private final UsuarioRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final EstoqueRepository estoqueRepository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoRepository pagamentoRepository;
    private final PagamentoService pagamentoService;

    public PedidoService(
            UsuarioRepository usuarioRepository,
            ProdutoRepository produtoRepository,
            EstoqueRepository estoqueRepository,
            PedidoRepository pedidoRepository,
            PagamentoRepository pagamentoRepository,
            PagamentoService pagamentoService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.estoqueRepository = estoqueRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagamentoRepository = pagamentoRepository;
        this.pagamentoService = pagamentoService;
    }

    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pedido não encontrado: " + id
                ));
    }

    /*
     * Uma única transação envolve usuário, produto, estoque,
     * pedido e pagamento.
     */
    @Transactional
    public Pedido criar(CriarPedidoRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado: " + request.usuarioId()
                ));

        if (!usuario.isAtivo()) {
            throw new IllegalArgumentException("Usuário inativo");
        }

        Pedido pedido = new Pedido(usuario);
        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.itens()) {
            Produto produto = produtoRepository.findById(itemRequest.produtoId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Produto não encontrado: " + itemRequest.produtoId()
                    ));

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException(
                        "Produto inativo: " + produto.getNome()
                );
            }

            Estoque estoque = estoqueRepository.findByProdutoId(produto.getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException(
                            "Estoque não encontrado para o produto: " + produto.getId()
                    ));

            if (estoque.getQuantidade() < itemRequest.quantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto: " + produto.getNome()
                );
            }

            // A baixa ocorre antes do pagamento.
            estoque.setQuantidade(
                    estoque.getQuantidade() - itemRequest.quantidade()
            );
            estoqueRepository.save(estoque);

            ItemPedido item = new ItemPedido(
                    produto,
                    itemRequest.quantidade(),
                    produto.getPreco()
            );

            pedido.adicionarItem(item);
            total = total.add(item.getSubtotal());
        }

        pedido.setValorTotal(total);
        pedido.setStatus("AGUARDANDO_PAGAMENTO");
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        Pagamento pagamento = pagamentoService.processar(
                pedidoSalvo,
                usuario,
                total,
                request.formaPagamento(),
                request.numeroCartao()
        );

        // Dependência direta do resultado persistido por outro service.
        Pagamento pagamentoConsultado = pagamentoRepository
                .findByPedidoId(pedidoSalvo.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Pagamento não foi persistido"
                ));

        if (!"APROVADO".equals(pagamentoConsultado.getStatus())) {
            pedidoSalvo.setStatus("PAGAMENTO_RECUSADO");
            pedidoRepository.save(pedidoSalvo);

            throw new PagamentoRecusadoException(
                    "Pagamento recusado: " + pagamento.getMotivo()
            );
        }

        pedidoSalvo.setStatus("PAGO");
        return pedidoRepository.save(pedidoSalvo);
    }
}
