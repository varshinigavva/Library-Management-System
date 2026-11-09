package com.library;

import com.library.model.LibraryUser;
import com.library.service.LibraryService;
import com.library.storage.FileStorageService;
import com.library.ui.LoginFrame;
import com.library.ui.MainFrame;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Utility that generates high-resolution PNG screenshots of all application panels
 * for live demonstration documentation and README embedding.
 */
public class ScreenshotGenerator {

    public static void main(String[] args) {
        // Set Look and Feel for crisp rendering
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        File outDir = new File("screenshots");
        if (!outDir.exists()) outDir.mkdirs();

        try {
            FileStorageService storage = new FileStorageService("data/library_data.ser");
            LibraryService service = new LibraryService(storage);

            // 1. Capture Login Frame
            LoginFrame loginFrame = new LoginFrame(service);
            captureComponent(loginFrame, 520, 620, new File(outDir, "01_login_screen.png"));
            loginFrame.dispose();

            // 2. Capture Librarian Panels
            LibraryUser adminUser = service.authenticate("admin", "admin123");
            MainFrame adminFrame = new MainFrame(service, adminUser);
            adminFrame.setSize(1240, 800);
            adminFrame.doLayout();
            adminFrame.validate();

            // Dashboard
            adminFrame.navigateTo(MainFrame.NAV_DASHBOARD);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "02_librarian_dashboard.png"));

            // Books
            adminFrame.navigateTo(MainFrame.NAV_BOOKS);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "03_book_catalog.png"));

            // Users
            adminFrame.navigateTo(MainFrame.NAV_USERS);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "04_user_directory.png"));

            // Borrowings
            adminFrame.navigateTo(MainFrame.NAV_BORROWINGS);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "05_borrowing_management.png"));

            // Returns
            adminFrame.navigateTo(MainFrame.NAV_RETURNS);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "06_return_processing.png"));

            // Reports
            adminFrame.navigateTo(MainFrame.NAV_REPORTS);
            captureComponent(adminFrame, 1240, 800, new File(outDir, "07_circulation_reports.png"));
            adminFrame.dispose();

            // 3. Capture Student Portal (Alex Chen - 4 Loans)
            LibraryUser studentUser = service.authenticate("REG-2023-CS-001", "student123");
            MainFrame studentFrame = new MainFrame(service, studentUser);
            studentFrame.setSize(1240, 800);
            studentFrame.doLayout();
            studentFrame.validate();

            studentFrame.navigateTo(MainFrame.NAV_DASHBOARD);
            captureComponent(studentFrame, 1240, 800, new File(outDir, "08_student_portal.png"));
            studentFrame.dispose();

            System.out.println("All screenshots successfully captured into screenshots/ directory.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void captureComponent(Window window, int width, int height, File outFile) throws Exception {
        window.setSize(width, height);
        window.addNotify();
        window.validate();
        window.doLayout();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);

        if (window instanceof RootPaneContainer) {
            JRootPane rp = ((RootPaneContainer) window).getRootPane();
            rp.setSize(width, height);
            rp.validate();
            rp.paint(g2);
        } else {
            window.paint(g2);
        }
        g2.dispose();

        ImageIO.write(image, "png", outFile);
        System.out.println("Generated: " + outFile.getName() + " (" + outFile.length() + " bytes)");
    }
}
