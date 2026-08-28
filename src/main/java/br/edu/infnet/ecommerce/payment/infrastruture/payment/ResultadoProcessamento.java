package br.edu.infnet.ecommerce.payment.infrastruture.payment;

public record ResultadoProcessamento(
        boolean aprovado,
        String motivo,
        String codigoAutorizacao
) {

    public static ResultadoProcessamento aprovado(
            String codigoAutorizacao
    ) {
        return new ResultadoProcessamento(
                true,
                null,
                codigoAutorizacao
        );
    }

    public static ResultadoProcessamento recusado(
            String motivo
    ) {
        return new ResultadoProcessamento(
                false,
                motivo,
                null
        );
    }
}