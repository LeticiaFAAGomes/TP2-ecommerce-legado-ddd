package br.edu.infnet.ecommerce.payment.infrastruture.payment;

import br.edu.infnet.ecommerce.payment.application.port.ProcessadorPagamento;
import br.edu.infnet.ecommerce.payment.domain.valueObject.Dinheiro;
import br.edu.infnet.ecommerce.payment.domain.valueObject.NumeroCartao;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class ProcessadorPagamentoImpl implements ProcessadorPagamento {

    private static final BigDecimal LIMITE = new BigDecimal("10000.00");

    @Override
    public ResultadoProcessamento processar(
            Dinheiro valor,
            NumeroCartao numeroCartao
    ) {

        if (valor == null || valor.menorOuIgualAZero()) {
            return ResultadoProcessamento.recusado(
                    "VALOR_INVALIDO"
            );
        }

        if (valor.maiorQue(LIMITE)) {
            return ResultadoProcessamento.recusado(
                    "LIMITE_EXCEDIDO"
            );
        }

        if (numeroCartao == null) {
            return ResultadoProcessamento.recusado(
                    "CARTAO_INVALIDO"
            );
        }

        if (numeroCartao.estaBloqueado()) {
            return ResultadoProcessamento.recusado(
                    "CARTAO_BLOQUEADO"
            );
        }

        return ResultadoProcessamento.aprovado(
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );
    }
}
