package br.edu.infnet.ecommerce.payment.domain.valueObject;

import java.util.Objects;

public record PagamentoId(Long valor) {

    public PagamentoId {
        if (valor != null && valor <= 0) {
            throw new IllegalArgumentException("O id do pagamento deve ser positivo");
        }
    }

    public static PagamentoId de(Long valor) {
        return new PagamentoId(valor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof PagamentoId(Long outroValor))) {
            return false;
        }

        return Objects.equals(valor, outroValor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }
}