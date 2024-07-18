package io.openfuture.openmessenger.service


import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import io.openfuture.openmessenger.repository.WalletRepository
import io.openfuture.openmessenger.repository.entity.BlockchainType
import io.openfuture.openmessenger.repository.entity.WalletEntity
import io.openfuture.openmessenger.service.dto.CreateWalletRequest
import io.openfuture.openmessenger.service.dto.DecryptWalletRequest
import io.openfuture.openmessenger.service.dto.KeyResponse
import io.openfuture.openmessenger.service.response.WalletResponse
import io.openfuture.openmessenger.util.getDerivedKey
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.bitcoinj.core.Address
import org.bitcoinj.crypto.ChildNumber
import org.bitcoinj.params.MainNetParams
import org.bitcoinj.script.Script
import org.bitcoinj.wallet.DeterministicSeed
import org.springframework.stereotype.Service
import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.spec.InvalidKeySpecException
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


@Service
class WalletManagementService(
    private val walletRepository: WalletRepository
) {

    val salt = "NaCl"
    val initVector = "IIIIIIIIIIIIIIII"
    val iterations = 1000
    val keyLength = 256
    fun generate(request: CreateWalletRequest, username: String): WalletResponse {
        // Generate mnemonic words
        val seedCode = generateSeedCode()

        // Generate address for blockchain
        val blockchain = generateAddress(seedCode, request.blockchainType)

        val encryptedPrivateKey = encrypt(blockchain.privateKey, request.password)
        val encryptedSeedPhrases = encrypt(seedCode, request.password)

        // Save address on db
        val wallet = walletRepository.save(WalletEntity(
            blockchain.address,
            encryptedPrivateKey,
            request.blockchainType,
            encryptedSeedPhrases,
            username
        ))

        return WalletResponse(wallet.address, blockchain.privateKey, request.blockchainType, seedCode)
    }


    fun getByUserId(userId: String): List<WalletResponse> {
        return walletRepository.findAllByUserId(userId)
            .map { WalletResponse(it.address, it.privateKey, it.blockchainType, it.seedPhrases) }
    }

    fun decryptWallet(decryptWalletRequest: DecryptWalletRequest): String{
        return decrypt(decryptWalletRequest.encryptedText, decryptWalletRequest.password)
    }

    fun generateSeedCode(): String {
        val seedCode = StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        SecureRandom().nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
            .createMnemonic(entropy, seedCode::append)

        return seedCode.toString()
    }

    fun generateAddress(seedCode: String, blockchainType: BlockchainType): KeyResponse {

        return when (blockchainType){
            BlockchainType.BNB, BlockchainType.ETH -> generateEthAddress(seedCode, blockchainType)
            BlockchainType.BTC -> generateBtcAddress(seedCode)
        }

    }

    fun generateEthAddress(seedCode: String, blockchainType: BlockchainType): KeyResponse {

        // Ex. m/44'/60'/0'/0 derivation path
        val derivationPath = getDerivationPath(blockchainType)

        val derivedKeyPair = getDerivedKey(derivationPath, seedCode)

        // Load the wallet for the derived keypair
        val credential = Credentials.create(derivedKeyPair)

        return KeyResponse(
            BlockchainType.ETH.getValue(),
            credential.ecKeyPair.privateKey.toString(16),
            credential.address
        )
    }

    fun generateBtcAddress(seedCode: String): KeyResponse {

        val creationTime = System.currentTimeMillis() / 1000L

        val seed = DeterministicSeed(seedCode, null, "", creationTime)

        val params = MainNetParams.get() //TestNet3Params.get()
        val wallet = org.bitcoinj.wallet.Wallet.fromSeed(
            params,
            seed,
            Script.ScriptType.P2PKH,
            listOf(
                ChildNumber(44, true),
                ChildNumber(0, true),
                ChildNumber.ZERO_HARDENED,
                ChildNumber.ZERO,
                ChildNumber.ZERO
            )
        )
        val privateKey = wallet.watchingKey.getPrivateKeyEncoded(params)

        return KeyResponse(
            BlockchainType.BTC.getValue(),
            privateKey.toString(),
            Address.fromKey(params, wallet.watchingKey, Script.ScriptType.P2PKH).toString()
        )
    }

    fun getDerivationPath(blockchainType: BlockchainType): IntArray {

        val derivationPath = when (blockchainType) {
            BlockchainType.ETH -> intArrayOf(
                44 or Bip32ECKeyPair.HARDENED_BIT,
                60 or Bip32ECKeyPair.HARDENED_BIT,
                0 or Bip32ECKeyPair.HARDENED_BIT,
                0,
                0
            )

            BlockchainType.BTC -> intArrayOf(
                44 or Bip32ECKeyPair.HARDENED_BIT,
                0 or Bip32ECKeyPair.HARDENED_BIT,
                0 or Bip32ECKeyPair.HARDENED_BIT,
                0,
                0
            )

            else -> intArrayOf(
                44 or Bip32ECKeyPair.HARDENED_BIT,
                714 or Bip32ECKeyPair.HARDENED_BIT,
                0 or Bip32ECKeyPair.HARDENED_BIT,
                0,
                0
            )
        }

        return derivationPath
    }

    fun encrypt(plainText: String, password: String): String{

        val passwordChars = password.toCharArray()
        val saltBytes = salt.toByteArray()

        val secretKey: SecretKey = getKey(passwordChars, saltBytes, iterations, keyLength)
        val key = secretKey.encoded

        return encrypt(key, initVector, plainText)!!
    }

    fun decrypt(encryptedText: String, password: String): String{
        val passwordChars = password.toCharArray()
        val saltBytes = salt.toByteArray()
        val secretKey: SecretKey = getKey(passwordChars, saltBytes, iterations, keyLength)
        val key = secretKey.encoded
        val decrypted: String = decrypt(key, initVector, encryptedText)!!
        println("Decrypted string: $decrypted")
        return decrypted
    }

    fun getKey(password: CharArray?, salt: ByteArray?, iterations: Int, keyLength: Int): SecretKey {
        return try {
            val skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val spec = PBEKeySpec(password, salt, iterations, keyLength)
            skf.generateSecret(spec)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidKeySpecException) {
            throw RuntimeException(e)
        }
    }

    fun encrypt(key: ByteArray?, initVector: String, value: String): String? {
        try {
            val iv = IvParameterSpec(initVector.toByteArray())
            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv)
            val encrypted = cipher.doFinal(value.toByteArray())
            println("encrypted string base64: " + Base64.encodeBase64String(encrypted))
            println("encrypted string hex: " + Hex.encodeHexString(encrypted))
            return Base64.encodeBase64String(encrypted)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun decrypt(key: ByteArray?, initVector: String, encrypted: String?): String? {
        try {
            val iv = IvParameterSpec(initVector.toByteArray(StandardCharsets.UTF_8))
            val skeySpec = SecretKeySpec(key, "AES")
            val cipher = Cipher.getInstance("AES/CTR/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)
            val original = cipher.doFinal(Base64.decodeBase64(encrypted))
            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

}