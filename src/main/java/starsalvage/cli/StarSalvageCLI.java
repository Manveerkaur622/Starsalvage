package starsalvage.cli;

import starsalvage.engine.Direction;
import starsalvage.engine.GameEngine;
import starsalvage.engine.GamePersistence;
import starsalvage.engine.MoveResult;

import java.nio.file.Path;
import java.util.Scanner;

public class StarSalvageCLI {
    public static void main(String[] args) {
        GameEngine engine = new GameEngine();
        Scanner scanner = new Scanner(System.in);
        System.out.println("StarSalvage CLI");
        System.out.println("Commands: north/south/east/west, wait, undo, save <file>, load <file>, status, help, quit");
        printGame(engine);
        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("quit") || input.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye.");
                break;
            }
            if (input.equalsIgnoreCase("help")) {
                System.out.println("Goal: collect all salvage crates (*) then reach the exit (E). Enemies: d dormant, A alert, H hunting.");
                continue;
            }
            if (input.equalsIgnoreCase("status")) {
                printGame(engine);
                continue;
            }
            if (input.equalsIgnoreCase("wait")) {
                MoveResult result = engine.waitTurn();
                System.out.println(result.getMessage());
                printGame(engine);
                continue;
            }
            if (input.equalsIgnoreCase("undo")) {
                MoveResult result = engine.undo();
                System.out.println(result.getMessage());
                printGame(engine);
                continue;
            }
            if (input.toLowerCase().startsWith("save ")) {
                String file = input.substring(5).trim();
                try {
                    GamePersistence.save(engine, Path.of(file));
                    System.out.println("Saved to " + file);
                } catch (Exception ex) {
                    System.out.println("Save failed: " + ex.getMessage());
                }
                continue;
            }
            if (input.toLowerCase().startsWith("load ")) {
                String file = input.substring(5).trim();
                try {
                    engine = GamePersistence.load(Path.of(file));
                    System.out.println("Loaded from " + file);
                    printGame(engine);
                } catch (Exception ex) {
                    System.out.println("Load failed: " + ex.getMessage());
                }
                continue;
            }
            Direction direction = Direction.fromCommand(input);
            if (direction != null) {
                MoveResult result = engine.move(direction);
                System.out.println(result.getMessage());
                printGame(engine);
            } else {
                System.out.println("Unknown command. Type help for options.");
            }
        }
    }

    private static void printGame(GameEngine engine) {
        System.out.println(engine.boardAsText());
        System.out.println(engine.statusText());
    }
}
