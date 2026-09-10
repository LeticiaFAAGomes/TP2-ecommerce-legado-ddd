package br.edu.infnet.ecommerce.payment.domain.event;

import java.time.LocalDateTime;

public record PagamentoAprovadoEvent(Long pagamentoId, Long pedidoId, LocalDateTime occurredOn
) implements DomainEvent {
}
