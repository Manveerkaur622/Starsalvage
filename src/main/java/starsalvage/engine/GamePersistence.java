package starsalvage.engine;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Path;

public final class GamePersistence {
    private GamePersistence() {
    }

    public static void save(GameEngine engine, Path path) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            output.writeObject(engine);
        }
    }

    public static GameEngine load(Path path) throws IOException, ClassNotFoundException {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            Object object = input.readObject();
            if (!(object instanceof GameEngine)) {
                throw new IOException("Saved file does not contain a StarSalvage game.");
            }
            return (GameEngine) object;
        }
    }
}
