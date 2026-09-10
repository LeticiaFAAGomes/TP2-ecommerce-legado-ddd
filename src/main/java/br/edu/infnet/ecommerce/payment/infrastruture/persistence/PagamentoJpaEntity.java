package br.edu.infnet.ecommerce.payment.infrastruture.persistence;

import br.edu.infnet.ecommerce.payment.domain.enums.FormaPagamento;
import br.edu.infnet.ecommerce.payment.domain.enums.StatusPagamento;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class PagamentoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "pedido_id", nullable = false, unique = true)
    private Long pedidoId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagamento", nullable = false)
    private FormaPagamento formaPagamento;
    @Column(name = "numero_cartao_mascarado")
    private String numeroCartaoMascarado;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;
    private String motivo;
    private String codigoAutorizacao;

    @Column(nullable = false)
    private LocalDateTime processadoEm;

    protected PagamentoJpaEntity() {
    }

    public Long getId() {
        return id;
    }
    public Long getPedidoId() {
        return pedidoId;
    }
    public void setPedidoId(Long pedidoId) {
        this.pedidoId = pedidoId;
    }
    public Long getUsuarioId() {
        return usuarioId;
    }
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    public BigDecimal getValor() {
        return valor;
    }
    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }
    public void setFormaPagamento(FormaPagamento formaPagamento) {
        this.formaPagamento = formaPagamento;
    }
    public String getNumeroCartaoMascarado() {
        return numeroCartaoMascarado;
    }

    public void setNumeroCartaoMascarado(String numeroCartaoMascarado) {
        this.numeroCartaoMascarado = numeroCartaoMascarado;
    }

    public StatusPagamento getStatus() {
        return status;
    }
    public void setStatus(StatusPagamento status) {
        this.status = status;
    }
    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
    public String getCodigoAutorizacao() {
        return codigoAutorizacao;
    }
    public void setCodigoAutorizacao(String codigoAutorizacao) {
        this.codigoAutorizacao = codigoAutorizacao;
    }
    public LocalDateTime getProcessadoEm() {
        return processadoEm;
    }
    public void setProcessadoEm(LocalDateTime processadoEm) {
        this.processadoEm = processadoEm;
    }
}