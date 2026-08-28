package br.edu.infnet.ecommerce.payment.domain.repository;

import br.edu.infnet.ecommerce.payment.domain.model.Pagamento;
import br.edu.infnet.ecommerce.payment.domain.valueObject.PagamentoId;

import java.util.Optional;


public interface PagamentoRepository {

    Pagamento salvar(Pagamento pagamento);

    Optional<Pagamento> buscarPorId(PagamentoId id);

    Optional<Pagamento> buscarPorPedidoId(Long pedidoId);
}