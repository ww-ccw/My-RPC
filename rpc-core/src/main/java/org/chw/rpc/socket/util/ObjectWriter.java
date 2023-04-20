package org.chw.rpc.socket.util;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.enumeration.PackageType;
import org.chw.rpc.serializer.CommonSerializer;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @Author CHW
 */
public class ObjectWriter {

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream outputStream, Object object, CommonSerializer serializer) throws IOException {

        outputStream.write(intToBytes(MAGIC_NUMBER));
        if (object instanceof RpcRequest) {
            outputStream.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        } else {
            outputStream.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        outputStream.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        outputStream.write(intToBytes(bytes.length));
        outputStream.write(bytes);
        outputStream.flush();

    }
    
    /**
     * 整数（int）转换成字节数组（byte[]）是为了将其在网络上传输时按照小端字节序进行存储，使用小端序使之能与Netty服务端通信。
     */
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value>>24) & 0xFF);
        src[1] = (byte) ((value>>16)& 0xFF);
        src[2] = (byte) ((value>>8)&0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }
}
