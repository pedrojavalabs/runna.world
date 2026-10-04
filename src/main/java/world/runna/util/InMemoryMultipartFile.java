package world.runna.util;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
public class InMemoryMultipartFile implements MultipartFile {
    private final String name; private final byte[] bytes;
    public InMemoryMultipartFile(String name, byte[] bytes){this.name=name; this.bytes=bytes;}
    public String getName(){return name;} public String getOriginalFilename(){return name;}
    public String getContentType(){return "application/gpx+xml";} public boolean isEmpty(){return bytes.length==0;}
    public long getSize(){return bytes.length;} public byte[] getBytes(){return bytes;}
    public InputStream getInputStream(){return new ByteArrayInputStream(bytes);}
    public void transferTo(File dest) throws IOException {try(FileOutputStream fos=new FileOutputStream(dest)){fos.write(bytes);}}
}
