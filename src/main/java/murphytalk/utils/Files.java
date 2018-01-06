package murphytalk.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipFile;

public class Files {
    public static Path getPathFromClassPath(Class clz, String filename) throws URISyntaxException {
        return Paths.get(clz.getResource(filename).toURI());
    }

    public static File getFileFromClassPath(Class clz, String filename) throws URISyntaxException {
        return getPathFromClassPath(clz,filename).toFile();
    }

    public static void readTextFileInZip(File zipFile,String filename, Consumer<Stream<String>> handler)  {
        try (final ZipFile zip = new ZipFile(zipFile)) {
            zip.stream()
                    .filter( ze -> !ze.isDirectory() )
                    .filter( ze -> ze.getName().equals(filename))
                    .findFirst().ifPresent( ze -> {
                        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(zip.getInputStream(ze)))) {
                            handler.accept(buffer.lines());
                        } catch (IOException e) {}
                     });
        }
        catch (IOException e){}
    }
}
