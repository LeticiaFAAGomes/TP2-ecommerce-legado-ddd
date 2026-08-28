package br.edu.infnet.ecommerce.payment.domain.model;

import br.edu.infnet.ecommerce.payment.domain.enums.FormaPagamento;
import br.edu.infnet.ecommerce.payment.domain.enums.StatusPagamento;
import br.edu.infnet.ecommerce.payment.domain.valueObject.Dinheiro;
import br.edu.infnet.ecommerce.payment.domain.valueObject.NumeroCartao;
import br.edu.infnet.ecommerce.payment.domain.valueObject.PagamentoId;

import java.time.LocalDateTime;


public class Pagamento {

    private PagamentoId id;
    private Long pedidoId;
    private Long usuarioId;
    private Dinheiro valor;
    private FormaPagamento formaPagamento;
    private NumeroCartao numeroCartao;
    private StatusPagamento status;
    private String motivo;
    private String codigoAutorizacao;
    private LocalDateTime processadoEm;

    protected Pagamento() {
    }

    private Pagamento(
            Long pedidoId,
            Long usuarioId,
            Dinheiro valor,
            FormaPagamento formaPagamento,
            NumeroCartao numeroCartao
    ) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("O pedido é obrigatório");
        }

        if (usuarioId == null) {
            throw new IllegalArgumentException("O usuário é obrigatório");
        }

        if (valor == null) {
            throw new IllegalArgumentException("O valor é obrigatório");
        }

        if (formaPagamento == null) {
            throw new IllegalArgumentException("A forma de pagamento é obrigatória");
        }

        this.pedidoId = pedidoId;
        this.usuarioId = usuarioId;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.numeroCartao = numeroCartao;
        this.status = StatusPagamento.PENDENTE;
    }

    public static Pagamento criar(
            Long pedidoId,
            Long usuarioId,
            Dinheiro valor,
            FormaPagamento formaPagamento,
            NumeroCartao numeroCartao
    ) {
        return new Pagamento(
                pedidoId,
                usuarioId,
                valor,
                formaPagamento,
                numeroCartao
        );
    }

    public static Pagamento reconstruir(
        PagamentoId id,
        Long pedidoId,
        Long usuarioId,
        Dinheiro valor,
        FormaPagamento formaPagamento,
        NumeroCartao numeroCartao,
        StatusPagamento status,
        String motivo,
        String codigoAutorizacao,
        LocalDateTime processadoEm
) {
    Pagamento pagamento = new Pagamento(
            pedidoId,
            usuarioId,
            valor,
            formaPagamento,
            numeroCartao
    );

    pagamento.id = id;
    pagamento.status = status;
    pagamento.motivo = motivo;
    pagamento.codigoAutorizacao = codigoAutorizacao;
    pagamento.processadoEm = processadoEm;

    return pagamento;
}

    public void aprovar(String codigoAutorizacao) {
        this.status = StatusPagamento.APROVADO;
        this.motivo = null;
        this.codigoAutorizacao = codigoAutorizacao;
        this.processadoEm = LocalDateTime.now();
    }

    public void recusar(String motivo) {
        this.status = StatusPagamento.RECUSADO;
        this.motivo = motivo;
        this.codigoAutorizacao = null;
        this.processadoEm = LocalDateTime.now();
    }

    public PagamentoId getId() {
        return id;
    }

    public Long getPedidoId() {
        return pedidoId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Dinheiro getValor() {
        return valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public NumeroCartao getNumeroCartao() {
        return numeroCartao;
    }

    public StatusPagamento getStatus() {
        return status;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getCodigoAutorizacao() {
        return codigoAutorizacao;
    }

    public LocalDateTime getProcessadoEm() {
        return processadoEm;
    }
}
