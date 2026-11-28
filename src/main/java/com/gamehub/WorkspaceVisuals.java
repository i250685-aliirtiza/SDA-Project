package com.gamehub;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/** Original artwork for the gaming floor and workspace navigation. */
final class WorkspaceVisuals {
    private WorkspaceVisuals() {}

    static Node icon(String name) {
        Canvas canvas = new Canvas(18, 18);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setStroke(Color.web("#93a7bd"));
        g.setLineWidth(1.35);
        g.setLineCap(StrokeLineCap.ROUND);
        g.setLineJoin(StrokeLineJoin.ROUND);
        switch (name) {
            case "HOME" -> {
                for (int x : new int[]{2, 10}) {
                    for (int y : new int[]{2, 10}) g.strokeRoundRect(x, y, 6, 6, 1.5, 1.5);
                }
            }
            case "ACCOUNTS", "TEAMS" -> {
                g.strokeOval(4, 2, 6, 6);
                g.strokeRoundRect(2, 10, 10, 5.5, 4, 4);
                line(g, 13, 4, 15, 5, 15, 7, 13, 8);
                line(g, 14, 10, 16, 12, 16, 15);
            }
            case "RESERVATIONS", "calendar" -> {
                g.strokeRoundRect(2, 4, 14, 12, 3, 3);
                line(g, 5, 2, 5, 6);
                line(g, 13, 2, 13, 6);
                line(g, 2, 8, 16, 8);
                line(g, 6, 12, 8, 14, 12, 10);
            }
            case "SESSIONS" -> {
                g.strokeOval(2.5, 2.5, 13, 13);
                line(g, 9, 5.5, 9, 9, 12, 11);
            }
            case "PAYMENTS", "MEMBERSHIP" -> {
                g.strokeRoundRect(2, 4, 14, 10, 3, 3);
                line(g, 2, 8, 16, 8);
                line(g, 5, 11, 8, 11);
            }
            case "EVENTS", "TOURNAMENTS" -> {
                line(g, 5, 2, 13, 2, 13, 6, 12, 9, 9, 11, 6, 9, 5, 6, 5, 2);
                line(g, 5, 4, 2, 4, 2, 7, 5, 8);
                line(g, 13, 4, 16, 4, 16, 7, 13, 8);
                line(g, 9, 11, 9, 15);
                line(g, 5, 15, 13, 15);
            }
            case "MATCHES" -> {
                line(g, 2, 2, 6, 2, 6, 7, 10, 7, 10, 11, 16, 11);
                line(g, 2, 6, 6, 6);
                line(g, 2, 12, 6, 12, 6, 15, 10, 15, 10, 11);
                line(g, 2, 16, 6, 16);
            }
            case "STATIONS" -> {
                g.strokeRoundRect(2, 3, 14, 9, 2, 2);
                line(g, 9, 12, 9, 15);
                line(g, 5, 15, 13, 15);
            }
            case "MAINTENANCE" -> {
                line(g, 3, 3, 7, 3, 9, 6, 7, 9, 3, 8, 2, 5);
                line(g, 7, 9, 13, 16, 16, 13, 9, 6);
            }
            case "REPORTS" -> {
                line(g, 3, 3, 3, 15, 16, 15);
                line(g, 6, 12, 6, 9);
                line(g, 10, 12, 10, 6);
                line(g, 14, 12, 14, 3);
            }
            case "logout" -> {
                line(g, 8, 3, 3, 3, 3, 15, 8, 15);
                line(g, 7, 9, 16, 9, 13, 6);
                line(g, 16, 9, 13, 12);
            }
            default -> {
                g.setStroke(Color.web("#132018"));
                line(g, 3, 9, 15, 9, 11, 5);
                line(g, 15, 9, 11, 13);
            }
        }
        canvas.setMouseTransparent(true);
        return canvas;
    }

    static Node mark() {
        Canvas canvas = new Canvas(32, 32);
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.web("#c5f580"));
        g.fillRoundRect(0, 0, 32, 32, 9, 9);
        g.setStroke(Color.web("#15211a"));
        g.setLineWidth(2.35);
        g.setLineJoin(StrokeLineJoin.ROUND);
        g.setLineCap(StrokeLineCap.ROUND);
        line(g, 21, 8, 11, 8, 7.5, 12, 7.5, 20, 11, 24, 21, 24, 24, 20, 24, 16, 16, 16);
        return canvas;
    }

    static Region floor() {
        return new Region() {
            private final Canvas canvas = new Canvas();
            {
                getChildren().add(canvas);
                setMouseTransparent(true);
                setMinSize(0, 0);
            }

            @Override protected void layoutChildren() {
                double width = getWidth(), height = getHeight();
                if (width < 1 || height < 1) return;
                canvas.setWidth(width);
                canvas.setHeight(height);
                paintFloor(canvas.getGraphicsContext2D(), width, height);
            }
        };
    }

    private static void paintFloor(GraphicsContext g, double width, double height) {
        g.setFill(gradient(0, 0, 1, 1, "#101a29", "#172568", "#0d1424"));
        g.fillRect(0, 0, width, height);
        g.setFill(new RadialGradient(0, 0, 0.79, 0.42, 0.62, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#4465f0", 0.83)), new Stop(1, Color.TRANSPARENT)));
        g.fillRect(0, 0, width, height);

        double vanishingX = width * 0.73;
        double horizon = height * 0.56;
        g.setLineWidth(1);
        g.setStroke(Color.web("#93a8ff", 0.19));
        for (int step = -10; step <= 10; step++) {
            double floorX = vanishingX + step * width / 8;
            g.strokeLine(vanishingX, horizon, floorX, height);
        }
        for (int step = 1; step < 12; step++) {
            double y = horizon + (height - horizon) * step * step / 121;
            g.strokeLine(0, y, width, y);
        }
        for (int step = 1; step <= 6; step++) {
            double x = width * (0.53 + step * 0.091);
            g.strokeLine(x, 0, (x + vanishingX) / 2, horizon);
        }

        boolean tall = height > 400;
        double scale = Math.min(width * (tall ? 0.0028 : 0.0023), height * 0.0058);
        g.save();
        g.translate(width * (tall ? 0.28 : 0.55), height * (tall ? 0.48 : 0.63));
        g.scale(scale, scale);
        station(g, 165, -82, 0.52, "#8195ff");
        station(g, 110, -44, 0.72, "#78a5ff");
        station(g, 35, 6, 1.0, "#c5f580");
        g.restore();

        g.setFill(new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#101a29")), new Stop(0.33, Color.web("#101a29", 0.91)),
                new Stop(0.61, Color.web("#101a29", 0.15)), new Stop(1, Color.TRANSPARENT)));
        g.fillRect(0, 0, width, height);
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.TRANSPARENT), new Stop(0.68, Color.TRANSPARENT),
                new Stop(1, Color.web("#101a29", height > 400 ? 0.98 : 0.38))));
        g.fillRect(0, 0, width, height);
    }

    private static void station(GraphicsContext g, double x, double y, double scale, String light) {
        g.save();
        g.translate(x, y);
        g.scale(scale, scale);
        g.setLineWidth(1.25);
        g.setLineJoin(StrokeLineJoin.ROUND);
        g.setStroke(Color.web("#6985d7"));
        polygon(g, "#111b34", 4, 11, 45, -11, 167, 14, 130, 40);
        polygon(g, "#10172b", 4, 11, 130, 40, 130, 49, 4, 20);
        polygon(g, "#1b2850", 130, 40, 167, 14, 167, 23, 130, 49);
        polygon(g, "#172444", 29, 27, 41, 30, 41, 66, 29, 62);
        polygon(g, "#172444", 126, 47, 138, 42, 138, 82, 126, 88);
        polygon(g, "#0a1228", 82, 7, 91, 9, 91, -13, 82, -15);
        polygon(g, "#25355f", 64, 5, 78, 0, 108, 7, 95, 13);
        polygon(g, "#1c2b52", 39, -67, 45, -72, 133, -51, 133, 3, 126, 8);
        polygon(g, "#0d1631", 39, -67, 126, -46, 126, 8, 39, -13);

        Color glow = Color.web(light);
        g.setFill(gradient(0, 0, 1, 1, "#141b44", "#273c8a", "#14295a"));
        g.fillPolygon(new double[]{45, 120, 120, 45}, new double[]{-60, -42, 1, -17}, 4);
        g.setStroke(glow.deriveColor(0, 1, 1, 0.84));
        line(g, 54, -28, 70, -44, 90, -23, 100, -30, 113, -14);
        g.setStroke(glow.deriveColor(0, 1, 1, 0.27));
        line(g, 54, -24, 113, -10);
        line(g, 54, -20, 113, -6);
        g.setStroke(Color.web("#687ec1"));
        polygon(g, "#17254d", 38, 16, 50, 9, 104, 22, 92, 30);
        g.setStroke(glow.deriveColor(0, 1, 1, 0.6));
        line(g, 49, 15, 91, 25);
        line(g, 54, 13, 98, 23);
        g.setStroke(glow);
        line(g, 6, 16, 130, 45, 164, 21);
        g.restore();
    }

    private static LinearGradient gradient(double x, double y, double endX, double endY, String start, String middle, String end) {
        return new LinearGradient(x, y, endX, endY, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web(start)), new Stop(0.63, Color.web(middle)), new Stop(1, Color.web(end)));
    }

    private static void polygon(GraphicsContext g, String fill, double... points) {
        double[] x = new double[points.length / 2];
        double[] y = new double[x.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = points[i * 2];
            y[i] = points[i * 2 + 1];
        }
        g.setFill(Color.web(fill));
        g.fillPolygon(x, y, x.length);
        g.strokePolygon(x, y, x.length);
    }

    private static void line(GraphicsContext g, double... points) {
        g.beginPath();
        g.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) g.lineTo(points[i], points[i + 1]);
        g.stroke();
    }
}
