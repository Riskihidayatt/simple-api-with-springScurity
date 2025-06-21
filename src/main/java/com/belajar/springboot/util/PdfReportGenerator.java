package com.belajar.springboot.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts; // Import baru

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PdfReportGenerator {

    public static byte[] generateCustomerSpendingReport(String customerName, LocalDate startDate, LocalDate endDate, BigDecimal totalAmount) throws IOException {
        try (PDDocument document = new PDDocument(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PDPage page = new PDPage();
            document.addPage(page);

            // Pilih font standar
            PDType1Font font = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(font, 14);
                contentStream.setLeading(14.5f);
                contentStream.newLineAtOffset(50, 700); // Posisi awal teks

                contentStream.showText("Laporan Pengeluaran Pelanggan");
                contentStream.newLine();
                contentStream.newLine();

                contentStream.setFont(font, 12);
                contentStream.showText("Pelanggan: " + (customerName != null ? customerName : "Semua Pelanggan"));
                contentStream.newLine();
                if (startDate != null && endDate != null) {
                    contentStream.showText("Periode: " + startDate.format(DateTimeFormatter.ISO_DATE) + " s/d " + endDate.format(DateTimeFormatter.ISO_DATE));
                    contentStream.newLine();
                } else {
                    contentStream.showText("Periode: Sepanjang Waktu");
                    contentStream.newLine();
                }
                contentStream.newLine();

                contentStream.showText("Total Pengeluaran (Paid): Rp " + totalAmount.toPlainString());
                contentStream.newLine();

                // Tambahkan konten lain jika perlu (misal tabel transaksi)

                contentStream.endText();
            }

            document.save(baos);
            return baos.toByteArray();
        }
    }
}