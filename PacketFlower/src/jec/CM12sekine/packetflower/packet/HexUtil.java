package jec.CM12sekine.packetflower.packet ;
public class HexUtil{
	public static String asHex(byte bytes[]) {
		StringBuffer strbuf = new StringBuffer(bytes.length * 2);

		for (int index = 0; index < bytes.length; index++) {
			int bt = bytes[index] & 0xff;
			if (bt < 0x10) {
				strbuf.append("0");
			}
			strbuf.append(Integer.toHexString(bt));
		}
		return strbuf.toString();
	}

	public static byte[] asByteArray(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for (int index = 0; index < bytes.length; index++) {
			bytes[index] = 	(byte) Integer.parseInt( hex.substring(index * 2, (index + 1) * 2), 16);
		}
		return bytes;
	}

}
