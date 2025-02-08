package com.app.banking.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.app.banking.constants.Constants;
import com.app.banking.dto.EmailDetails;
import com.app.banking.entity.Transaction;
import com.app.banking.entity.User;
import com.app.banking.repository.TransactionRepository;
import com.app.banking.repository.UserRepository;
import com.app.banking.service.impl.EmailService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatementUtil {

    private TransactionRepository transactionRepository;

    private UserRepository userRepository;

    private EmailService emailService;

    @Async
    public void generateStatement(String accountNumber, String startDate, String endDate) throws FileNotFoundException, DocumentException
    {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findByAccountNumber(accountNumber)
        .stream()
        .filter(transaction -> !transaction.getCreatedAt().isBefore(start))
        .filter(transaction -> !transaction.getCreatedAt().isAfter(end))
        .sorted(Comparator.comparing(Transaction::getCreatedAt))
        .toList();

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        log.info("Setting size for document");
        Document document = new Document(statementSize);
        OutputStream outputStream = new FileOutputStream(Constants.FILE); 
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfoTable = new PdfPTable(1);

        PdfPCell bankName = new PdfPCell(new Phrase("BANKING APPLICATION"));
        bankName.setBackgroundColor(BaseColor.CYAN);
        bankName.setPadding(10f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("Madurai, Tamil Nadu, India"));
        bankAddress.setBorder(0);

        bankInfoTable.addCell(bankName);
        bankInfoTable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);

        PdfPCell startDate1 = new PdfPCell(new Phrase("Start Date: "+startDate));
        startDate1.setBorder(0);

        PdfPCell statement = new PdfPCell(new Phrase("Statement Of Account"));
        statement.setBorder(0);

        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: "+endDate));
        stopDate.setBorder(0);

        PdfPCell accNo = new PdfPCell(new Phrase("Acc Num: "+user.getAccountNumber()));
        accNo.setBorder(0);

        PdfPCell name = new PdfPCell(new Phrase("Name: "+customerName));
        name.setBorder(0);

        PdfPCell space = new PdfPCell();
        space.setBorder(0);

        //PdfPCell address = new PdfPCell(new Phrase(" Address: "+user.getAddress()));
        //address.setBorder(0);

        PdfPTable transactionsTable = new PdfPTable(4);

        PdfPCell date = new PdfPCell(new Phrase("Date Of Transaction"));
        date.setBackgroundColor(BaseColor.CYAN);

        PdfPCell transactionType = new PdfPCell(new Phrase("Transaction Type"));
        transactionType.setBackgroundColor(BaseColor.CYAN);

        PdfPCell transactionAmount = new PdfPCell(new Phrase("Transaction Amount"));
        transactionAmount.setBackgroundColor(BaseColor.CYAN);

        PdfPCell status = new PdfPCell(new Phrase("Status"));
        status.setBackgroundColor(BaseColor.CYAN);

        transactionsTable.addCell(date);
        transactionsTable.addCell(transactionType);
        transactionsTable.addCell(transactionAmount);
        transactionsTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionsTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionsTable.addCell(new Phrase(transaction.getTransactionType()));
            transactionsTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionsTable.addCell(new Phrase(transaction.getStatus()));
        });
        
        statementInfo.addCell(space);
        statementInfo.addCell(statement);
        statementInfo.addCell(startDate1);
        statementInfo.addCell(accNo);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
       //statementInfo.addCell(address);

        document.add(bankInfoTable);
        document.add(statementInfo);
        document.add(transactionsTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
        .recipient(user.getEmail())
        .subject("Statement of Account")
        .messageBody("Kindly find your attached account statement for the duration from "+startDate+" to "+endDate)
        .attachment(Constants.FILE)
        .build();

        log.info("Email formed and sent to email service");
        emailService.sendEmailWithAttachment(emailDetails);
    }

}
