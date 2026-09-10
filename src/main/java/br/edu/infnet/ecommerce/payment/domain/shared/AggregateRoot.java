package br.edu.infnet.ecommerce.payment.domain.shared;

import br.edu.infnet.ecommerce.payment.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot {
    private final List<DomainEvent> eventos = new ArrayList<>();

    protected void registrarEvento(DomainEvent evento) {
        this.eventos.add(evento);
    }

    public List<DomainEvent> eventosOcorridos() {
        return List.copyOf(eventos);
    }

    public void limparEventos() {
        this.eventos.clear();
    }
}
