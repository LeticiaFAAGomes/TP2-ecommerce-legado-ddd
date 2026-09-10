package br.edu.infnet.ecommerce.payment.domain.valueObject;

import java.util.Objects;

public record PedidoId(Long valor) {

    public PedidoId {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException("O id do pedido deve ser positivo");
        }
    }

    public static PedidoId de(Long valor) {
        return new PedidoId(valor);
    }
}