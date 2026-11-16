package com.dsi.bolsines.dto;

/** flujo: codigo del flujo alternativo del CU (A1, A3, ...) o ERROR. */
public record ErrorResponse(String flujo, String mensaje) {
}
