package br.edu.infnet.ecommerce.payment.domain.valueObject;

import java.util.Objects;

public record NumeroCartao(String valor) {

    public NumeroCartao {
        Objects.requireNonNull(valor, "O número do cartão não pode ser nulo");

        if (valor.length() < 4) {
            throw new IllegalArgumentException("O cartão deve possuir pelo menos 4 dígitos");
        }
    }

    public boolean estaBloqueado() {
        return valor.endsWith("0000");
    }

    public boolean terminaCom1111() {
        return valor.endsWith("1111");
    }

    public String mascarado() {
        return "**** **** **** " + ultimosQuatroDigitos();
    }

    public String ultimosQuatroDigitos() {
        return valor.substring(valor.length() - 4);
    }
}
