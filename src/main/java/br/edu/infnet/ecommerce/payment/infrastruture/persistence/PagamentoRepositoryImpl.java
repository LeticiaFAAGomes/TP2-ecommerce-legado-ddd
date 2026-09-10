package br.edu.infnet.ecommerce.payment.infrastruture.persistence;

import br.edu.infnet.ecommerce.payment.domain.model.Pagamento;
import br.edu.infnet.ecommerce.payment.domain.repository.PagamentoRepository;

import br.edu.infnet.ecommerce.payment.domain.valueObject.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PagamentoRepositoryImpl implements PagamentoRepository {

    private final JpaPagamentoRepository jpaRepository;

    public PagamentoRepositoryImpl(
            JpaPagamentoRepository jpaRepository
    ) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pagamento salvar(Pagamento pagamento) {

        PagamentoJpaEntity entity = new PagamentoJpaEntity();

        entity.setPedidoId(pagamento.getPedidoId());
        entity.setUsuarioId(pagamento.getUsuarioId());
        entity.setValor(pagamento.getValor().valor());
        entity.setFormaPagamento(pagamento.getFormaPagamento());

        entity.setNumeroCartaoMascarado(
                pagamento.getNumeroCartao() != null
                        ? pagamento.getNumeroCartao().mascarado()
                        : null
        );

        entity.setStatus(pagamento.getStatus());
        entity.setMotivo(pagamento.getMotivo());
        entity.setCodigoAutorizacao(
                pagamento.getCodigoAutorizacao()
        );
        entity.setProcessadoEm(
                pagamento.getProcessadoEm()
        );

        PagamentoJpaEntity salvo = jpaRepository.save(entity);

        return Pagamento.reconstruir(
                PagamentoId.de(salvo.getId()),
                PedidoId.de(salvo.getPedidoId()),
                UsuarioId.de(salvo.getUsuarioId()),
                Dinheiro.de(salvo.getValor()),
                salvo.getFormaPagamento(),
                null,
                salvo.getStatus(),
                salvo.getMotivo(),
                salvo.getCodigoAutorizacao(),
                salvo.getProcessadoEm()
        );
    }

    @Override
    public Optional<Pagamento> buscarPorId(
            PagamentoId id
    ) {
        return jpaRepository.findById(id.valor())
                .map(this::toDomain);
    }

    @Override
    public Optional<Pagamento> buscarPorPedidoId(
            Long pedidoId
    ) {
        return jpaRepository.findByPedidoId(pedidoId)
                .map(this::toDomain);
    }

    private Pagamento toDomain(
            PagamentoJpaEntity entity
    ) {

        NumeroCartao numeroCartao = null;

        return Pagamento.reconstruir(
                PagamentoId.de(entity.getId()),
                PedidoId.de(entity.getPedidoId()),
                UsuarioId.de(entity.getUsuarioId()),
                Dinheiro.de(entity.getValor()),
                entity.getFormaPagamento(),
                numeroCartao,
                entity.getStatus(),
                entity.getMotivo(),
                entity.getCodigoAutorizacao(),
                entity.getProcessadoEm()
        );
    }
}