package br.edu.infnet.ecommerce.payment.domain.event;

import java.time.LocalDateTime;

public interface DomainEvent {
        LocalDateTime occurredOn();
}
