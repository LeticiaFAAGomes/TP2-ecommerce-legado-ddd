package br.edu.infnet.ecommerce.payment.application.port;

import br.edu.infnet.ecommerce.payment.domain.valueObject.Dinheiro;
import br.edu.infnet.ecommerce.payment.domain.valueObject.NumeroCartao;
import br.edu.infnet.ecommerce.payment.infrastruture.payment.ResultadoProcessamento;

public interface ProcessadorPagamento {

    ResultadoProcessamento processar(
            Dinheiro valor,
            NumeroCartao numeroCartao
    );
}
