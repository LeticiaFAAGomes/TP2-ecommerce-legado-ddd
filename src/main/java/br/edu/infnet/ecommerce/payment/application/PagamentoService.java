package br.edu.infnet.ecommerce.payment.application;

import br.edu.infnet.ecommerce.payment.application.port.ProcessadorPagamento;
import br.edu.infnet.ecommerce.payment.domain.enums.FormaPagamento;
import br.edu.infnet.ecommerce.payment.domain.model.Pagamento;
import br.edu.infnet.ecommerce.payment.domain.valueObject.Dinheiro;
import br.edu.infnet.ecommerce.payment.domain.valueObject.NumeroCartao;
import br.edu.infnet.ecommerce.payment.domain.valueObject.PedidoId;
import br.edu.infnet.ecommerce.payment.domain.valueObject.UsuarioId;
import br.edu.infnet.ecommerce.payment.infrastruture.payment.ResultadoProcessamento;
import br.edu.infnet.ecommerce.payment.domain.repository.PagamentoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;

    private final ProcessadorPagamento processadorPagamento;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            ProcessadorPagamento processadorPagamento
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.processadorPagamento = processadorPagamento;
    }

    public Pagamento processar(
            PedidoId pedidoId,
            UsuarioId usuarioId,
            BigDecimal valor,
            String formaPagamento,
            String numeroCartao
    ) {

        Dinheiro dinheiro = Dinheiro.de(valor);

        FormaPagamento forma = FormaPagamento.valueOf(
                formaPagamento.toUpperCase()
        );

        NumeroCartao cartao = new NumeroCartao(numeroCartao);

        Pagamento pagamento = Pagamento.criar(
                pedidoId,
                usuarioId,
                dinheiro,
                forma,
                cartao
        );

        ResultadoProcessamento resultado =
                processadorPagamento.processar(
                        dinheiro,
                        cartao
                );

        if (resultado.aprovado()) {
            pagamento.aprovar(
                    resultado.codigoAutorizacao()
            );
        } else {
            pagamento.recusar(
                    resultado.motivo()
            );
        }

        return pagamentoRepository.salvar(pagamento);
    }
}
