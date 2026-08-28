package br.edu.infnet.ecommerce.payment.infrastruture.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaPagamentoRepository
        extends JpaRepository<PagamentoJpaEntity, Long> {

    Optional<PagamentoJpaEntity> findByPedidoId(Long pedidoId);
}
