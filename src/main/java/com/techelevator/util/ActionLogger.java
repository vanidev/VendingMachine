package com.techelevator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ActionLogger {
    private static final String LOG_FILE_PATH = "Log.txt";
    private static final String LOG_ENTRY_FORMAT = "%s %s %s: $%.2f $%.2f" + System.lineSeparator();
    private static PrintWriter logFileWriter;

    public static void log(String action, BigDecimal amount, BigDecimal updatedCurrentMoney) {
        try {
            if (logFileWriter == null) {
                File logFile = new File(LOG_FILE_PATH);
                logFileWriter = new PrintWriter(new FileOutputStream(logFile, logFile.exists()));
            }
            LocalDateTime now = LocalDateTime.now();
            logFileWriter.printf(LOG_ENTRY_FORMAT,
                    now.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                    now.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)),
                    action,
                    amount.doubleValue(),
                    updatedCurrentMoney.doubleValue());
            logFileWriter.flush();
        }
        catch (Exception e) {
            throw new ActionLoggerException(e.getMessage());
        }
    }
}