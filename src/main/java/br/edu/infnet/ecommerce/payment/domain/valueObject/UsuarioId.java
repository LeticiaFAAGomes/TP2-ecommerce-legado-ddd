package br.edu.infnet.ecommerce.payment.domain.valueObject;

import java.util.Objects;

public record UsuarioId(Long valor) {

    public UsuarioId {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException("O id do usuário deve ser positivo");
        }
    }

    public static UsuarioId de(Long valor) {
        return new UsuarioId(valor);
    }
}