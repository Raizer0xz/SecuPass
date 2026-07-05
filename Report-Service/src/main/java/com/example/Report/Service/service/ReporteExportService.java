package com.example.Report.Service.service;

import com.example.Report.Service.model.ReporteFlujo;

// POI - Excel (imports específicos para evitar ambigüedad)
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

// OpenPDF - PDF (imports específicos para evitar ambigüedad)
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Chunk;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.draw.LineSeparator;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class ReporteExportService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────
    // EXCEL
    // ─────────────────────────────────────────────
    public byte[] generarExcel(ReporteFlujo reporte) {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Reporte SecuPass");
            sheet.setColumnWidth(0, 10000);
            sheet.setColumnWidth(1, 8000);

            // --- Estilo Título (blanco negrita sobre azul oscuro) ---
            CellStyle estileTitulo = workbook.createCellStyle();
            Font fuenteTituloBlanca = workbook.createFont();
            fuenteTituloBlanca.setBold(true);
            fuenteTituloBlanca.setFontHeightInPoints((short) 14);
            fuenteTituloBlanca.setColor(IndexedColors.WHITE.getIndex());
            estileTitulo.setFont(fuenteTituloBlanca);
            estileTitulo.setAlignment(HorizontalAlignment.CENTER);
            estileTitulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estileTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // --- Estilo Header (blanco negrita sobre gris) ---
            CellStyle estiloHeader = workbook.createCellStyle();
            Font fuenteHeader = workbook.createFont();
            fuenteHeader.setBold(true);
            fuenteHeader.setColor(IndexedColors.WHITE.getIndex());
            estiloHeader.setFont(fuenteHeader);
            estiloHeader.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setBorderBottom(BorderStyle.THIN);
            estiloHeader.setBorderTop(BorderStyle.THIN);

            // --- Estilo Valor normal ---
            CellStyle estiloValor = workbook.createCellStyle();
            estiloValor.setBorderBottom(BorderStyle.THIN);
            estiloValor.setBorderTop(BorderStyle.THIN);
            estiloValor.setBorderLeft(BorderStyle.THIN);
            estiloValor.setBorderRight(BorderStyle.THIN);

            // --- Estilo Estado (verde negrita) ---
            CellStyle estiloEstado = workbook.createCellStyle();
            Font fuenteEstado = workbook.createFont();
            fuenteEstado.setBold(true);
            fuenteEstado.setColor(IndexedColors.GREEN.getIndex());
            estiloEstado.setFont(fuenteEstado);
            estiloEstado.setBorderBottom(BorderStyle.THIN);
            estiloEstado.setBorderTop(BorderStyle.THIN);
            estiloEstado.setBorderLeft(BorderStyle.THIN);
            estiloEstado.setBorderRight(BorderStyle.THIN);

            // --- Fila título ---
            Row rowTitulo = sheet.createRow(0);
            rowTitulo.setHeightInPoints(24);
            Cell celdaTitulo = rowTitulo.createCell(0);
            celdaTitulo.setCellValue("REPORTE DE FLUJO FRONTERIZO - SECUPASS");
            celdaTitulo.setCellStyle(estileTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));

            // --- Fila vacía ---
            sheet.createRow(1);

            // --- Filas de datos ---
            String[][] datos = {
                    {"ID del Reporte",              String.valueOf(reporte.getId())},
                    {"Fecha de Inicio",             reporte.getFechaInicio().format(FORMATO_FECHA)},
                    {"Fecha de Fin",                reporte.getFechaFin().format(FORMATO_FECHA)},
                    {"Total Vehículos / Cruces",    String.valueOf(reporte.getTotalVehiculosCruces())},
                    {"Total Pasajeros Controlados", String.valueOf(reporte.getTotalPasajerosControlados())},
                    {"Total Declaraciones SAG",     String.valueOf(reporte.getTotalDeclaracionesSAG())},
                    {"Estado",                      reporte.getEstado()}
            };

            int filaIdx = 2;
            for (int i = 0; i < datos.length; i++) {
                Row row = sheet.createRow(filaIdx++);

                Cell header = row.createCell(0);
                header.setCellValue(datos[i][0]);
                header.setCellStyle(estiloHeader);

                Cell valor = row.createCell(1);
                valor.setCellValue(datos[i][1]);
                valor.setCellStyle(i == datos.length - 1 ? estiloEstado : estiloValor);
            }

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo Excel: " + e.getMessage(), e);
        }
    }

    // ─────────────────────────────────────────────
    // PDF
    // ─────────────────────────────────────────────
    public byte[] generarPdf(ReporteFlujo reporte) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 50, 50, 60, 60);
            PdfWriter.getInstance(document, out);
            document.open();

            // Título
            com.lowagie.text.Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16,
                    new java.awt.Color(13, 71, 161));
            Paragraph titulo = new Paragraph("REPORTE DE FLUJO FRONTERIZO", fuenteTitulo);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            com.lowagie.text.Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 11,
                    new java.awt.Color(100, 100, 100));
            Paragraph subtitulo = new Paragraph("Sistema SecuPass — SNA Chile", fuenteSubtitulo);
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20f);
            document.add(subtitulo);

            // Línea separadora
            LineSeparator linea = new LineSeparator();
            linea.setLineColor(new java.awt.Color(13, 71, 161));
            document.add(new Chunk(linea));
            document.add(Chunk.NEWLINE);

            // Tabla de datos
            PdfPTable tabla = new PdfPTable(2);
            tabla.setWidthPercentage(100);
            tabla.setWidths(new float[]{55f, 45f});
            tabla.setSpacingBefore(10f);

            com.lowagie.text.Font fuenteHeaderTabla = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10,
                    new java.awt.Color(255, 255, 255));
            com.lowagie.text.Font fuenteValor = FontFactory.getFont(FontFactory.HELVETICA, 10,
                    new java.awt.Color(33, 33, 33));
            com.lowagie.text.Font fuenteEstadoPdf = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10,
                    new java.awt.Color(27, 94, 32));

            java.awt.Color colorHeader    = new java.awt.Color(55, 71, 79);
            java.awt.Color colorFilaPar   = new java.awt.Color(240, 248, 255);
            java.awt.Color colorFilaImpar = java.awt.Color.WHITE;

            String[][] datos = {
                    {"ID del Reporte",              String.valueOf(reporte.getId())},
                    {"Fecha de Inicio",             reporte.getFechaInicio().format(FORMATO_FECHA)},
                    {"Fecha de Fin",                reporte.getFechaFin().format(FORMATO_FECHA)},
                    {"Total Vehículos / Cruces",    String.valueOf(reporte.getTotalVehiculosCruces())},
                    {"Total Pasajeros Controlados", String.valueOf(reporte.getTotalPasajerosControlados())},
                    {"Total Declaraciones SAG",     String.valueOf(reporte.getTotalDeclaracionesSAG())},
                    {"Estado",                      reporte.getEstado()}
            };

            // Cabecera de tabla
            PdfPCell cabecera1 = new PdfPCell(new Phrase("CAMPO", fuenteHeaderTabla));
            cabecera1.setBackgroundColor(colorHeader);
            cabecera1.setPadding(8f);
            tabla.addCell(cabecera1);

            PdfPCell cabecera2 = new PdfPCell(new Phrase("VALOR", fuenteHeaderTabla));
            cabecera2.setBackgroundColor(colorHeader);
            cabecera2.setPadding(8f);
            tabla.addCell(cabecera2);

            // Filas
            for (int i = 0; i < datos.length; i++) {
                boolean esEstado = i == datos.length - 1;
                java.awt.Color colorFila = (i % 2 == 0) ? colorFilaImpar : colorFilaPar;

                PdfPCell celdaLabel = new PdfPCell(new Phrase(datos[i][0], fuenteHeaderTabla));
                celdaLabel.setBackgroundColor(colorFila);
                celdaLabel.setPadding(7f);
                tabla.addCell(celdaLabel);

                PdfPCell celdaValor = new PdfPCell(
                        new Phrase(datos[i][1], esEstado ? fuenteEstadoPdf : fuenteValor));
                celdaValor.setBackgroundColor(colorFila);
                celdaValor.setPadding(7f);
                tabla.addCell(celdaValor);
            }

            document.add(tabla);

            // Pie de página
            document.add(Chunk.NEWLINE);
            com.lowagie.text.Font fuentePie = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8,
                    new java.awt.Color(150, 150, 150));
            Paragraph pie = new Paragraph("Documento generado automáticamente por SecuPass · SNA Chile", fuentePie);
            pie.setAlignment(Element.ALIGN_CENTER);
            document.add(pie);

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el archivo PDF: " + e.getMessage(), e);
        }
    }
}