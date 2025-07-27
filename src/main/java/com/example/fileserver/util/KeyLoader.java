package com.example.fileserver.util;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class KeyLoader {

    public static final String privateKeyPath = "-----BEGIN PRIVATE KEY-----\n" +
            "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCyYIeNTmU4O0WG\n" +
            "d+zjk+mHtdAMuZhIfu95SOSUNvy6OHSTOVZOVXOPpYUlhNFW4ETxlQ1h/SgOW/10\n" +
            "qze9rxdvCiau73xF63ihXkZ3AddKsa90LInm0cW59A6hOGTv5UKp37niC+qK3U4r\n" +
            "JP4bZ7qAU1PKCo9ogwfiaJl6QzhgGrvTP3+xNhgMSNLQuMOCJ83J7BibGYZ7zaT+\n" +
            "rb24VURsi9h4JtGc6DYWUiYF3fUyyU7dMIEjBj45sL7liVsu7SLObxJ+w6ZrdDR9\n" +
            "lx5mA76tj+7Tda6WbPl+7AY8LYsCWezc2kFZe4fnPD6XHIaJySwQ7WI/5SjvIxS6\n" +
            "jUpeNN2rAgMBAAECggEAUcXm3mS+ClVBvi9kLcA1Ir26rtq2cSWf6t37y+aZXVVc\n" +
            "O9HhOu/5ljSvIVAOF+GgOY+3HuDLaCnLu5xyZUbLW2Av86peMj+MgY2SiCQT6m4i\n" +
            "nQN+BI7OMj0sJ8SOoVtcgDvMH1CPKTHjD+xZgX7ABOwuUUUeES9gge6pzV7bjVJv\n" +
            "3inbwVru9zDJiu5GIbUTrFVABFf/MaA+wIfyTx4/p44FXn7DMYFntwo203QRPtCi\n" +
            "p25weFwycGnr4VJbe0XSJh4ZFsEl5EvVQYfX+VShrm+fugtGQOQEyiih2KRyPKed\n" +
            "CjUcc8zKPetSVFTg27iGHTPS2+pXWqe0w9x8gGtOgQKBgQDie+o4gFweAw93twXf\n" +
            "5yg+mLH+eYP+CJW5DTWhIZx8URtRFJe5fxR07unEXCWxs+oTxY5HykDUm7MNnMO8\n" +
            "YkwlFNOi9NAOpeO52XXVBn8sHVL/4mpNlV1dzsek3uvlSm7FXy6bzjm8sgdqsoZX\n" +
            "AhVaHLQO8EitdhOub7VI5COo2wKBgQDJn6StCd8NaU6eHFMj0ZKMjkz7NDpDAis0\n" +
            "kXetsISlBSKBFD7zKvptv4oS80fz/RnGA/VR7lt1KowxRg9wIBZ/lUWwUvS/BgBT\n" +
            "zv9cYbGsVqBElC0EQw+gv3euEzU3alRAuywvtchNp+IsaQqR9SqVv4O6lfL1PoNS\n" +
            "bOqct/+PcQKBgQCqXlJzjEM/JEg3YssdgdJpPA3xAlHC/4w5NvGXsyP29EAbPuVf\n" +
            "WXP2/l2FI1gLk+5KTzrXUBJynGkVnU40YLEs17zYAVadHjbu7+FOjOOPOMPdzuDu\n" +
            "WaRxU4IkyLgktFgdSptSAtC13CnuHjsmKmALeDoOXrwdGAx4FHoHSe51LwKBgCgJ\n" +
            "kZPi/+4T2U7AZSoZtbY/k73WczNXPOklcdSF1vFGAcIMGSwEftJIVMzKjqE+w110\n" +
            "78QjRvoPRuOJ/ArR9GXFaJ9I+dzs/iemVUMtCzEjypOTsRTFlFbhboyct9aXQL/R\n" +
            "60ZWHL5+9gTHnxe4zYoLC8eDe0mD7mPxyOzyBMehAoGBAN2lcd+4bzWE4OCIcAkZ\n" +
            "97PhaaeBW12yBXkZaZU6I0GoMGRXSwqP6aeSXiO2IomXTCkSlM232OA/RxaaDobj\n" +
            "iX5z6OhfpKGlopPh9Uj055Wer+D7pt10je0NEqVrW97ALVyB6PsASjf+8/aRvYGE\n" +
            "cu3yHcLrwiATeSxxMeyaFsQU\n" +
            "-----END PRIVATE KEY-----\n";

    private static final String publicKeyPath = "-----BEGIN PUBLIC KEY-----\n" +
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsmCHjU5lODtFhnfs45Pp\n" +
            "h7XQDLmYSH7veUjklDb8ujh0kzlWTlVzj6WFJYTRVuBE8ZUNYf0oDlv9dKs3va8X\n" +
            "bwomru98Ret4oV5GdwHXSrGvdCyJ5tHFufQOoThk7+VCqd+54gvqit1OKyT+G2e6\n" +
            "gFNTygqPaIMH4miZekM4YBq70z9/sTYYDEjS0LjDgifNyewYmxmGe82k/q29uFVE\n" +
            "bIvYeCbRnOg2FlImBd31MslO3TCBIwY+ObC+5YlbLu0izm8SfsOma3Q0fZceZgO+\n" +
            "rY/u03Wulmz5fuwGPC2LAlns3NpBWXuH5zw+lxyGicksEO1iP+Uo7yMUuo1KXjTd\n" +
            "qwIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    public static PrivateKey loadPrivateKey(String filename) throws Exception {
//        Files.readString(Path.of(filename))
        String key = privateKeyPath.replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static PublicKey loadPublicKey(String filename) throws Exception {
        String key =
                publicKeyPath
                .replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}