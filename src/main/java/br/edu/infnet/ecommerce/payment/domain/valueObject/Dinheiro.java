package br.edu.infnet.ecommerce.payment.domain.valueObject;

import java.math.BigDecimal;
import java.util.Objects;

public record Dinheiro(BigDecimal valor) {

    public Dinheiro {
        Objects.requireNonNull(valor, "O valor não pode ser nulo");

        valor = valor.setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public static Dinheiro de(BigDecimal valor) {
        return new Dinheiro(valor);
    }

    public boolean menorOuIgualAZero() {
        return valor.compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean maiorQue(BigDecimal outroValor) {
        return valor.compareTo(outroValor) > 0;
    }

    public boolean maiorQue(Dinheiro outro) {
        return valor.compareTo(outro.valor) > 0;
    }

    public BigDecimal valor() {
        return valor;
    }
}