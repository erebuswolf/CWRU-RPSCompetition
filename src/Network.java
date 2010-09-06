import java.io.IOException;
import java.io.InputStream;


public class Network {
	public static final byte [] getBytesOfLength(InputStream in, int length){
		byte [] input_bytes=new byte[length];
		int bytes_read=0;
		String input_values="";
		while(bytes_read<length){
			int read_temp=-1;

			try {
				read_temp = in.read(input_bytes,0,length-bytes_read);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(read_temp==-1){
				continue;
			}
			bytes_read+=read_temp;
			//System.out.println("bytes read: "+bytes_read);

			/*	for(int i=0;i<input_bytes.length;i++){
				System.out.print(input_bytes[i] +" ");
			}System.out.println();
			 */

			input_values+=(new String(input_bytes).substring(0, read_temp));
			//System.out.println("value read "+input_values +" "+input_values.length()+" "+ input_values.getBytes().length+" "+bytes_read);

			input_bytes=new byte[length];
		}
		return input_values.getBytes();
	}

	public static final int getByte(InputStream in){
		int byte_value=-1;
		while(byte_value==-1){
			try {
				byte_value=in.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return byte_value;
	}
	public static final byte[] intToByteArray(int value) {
		return new byte[] {
				(byte)(value >>> 24),
				(byte)(value >>> 16),
				(byte)(value >>> 8),
				(byte)value};
	}
	public static final int byteArrayToInt(byte [] b) {
		return ((b[0] << 24)
				+ ((b[1] & 0xFF) << 16)
				+ ((b[2] & 0xFF) << 8)
				+ (b[3] & 0xFF));
	}
}
