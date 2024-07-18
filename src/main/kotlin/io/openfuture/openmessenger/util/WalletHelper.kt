package io.openfuture.openmessenger.util


import org.web3j.crypto.*

fun getDerivedKey(derivationPath: IntArray, seedCode: String): Bip32ECKeyPair {
    // Generate a BIP32 master keypair from the mnemonic phrase
    val masterKeypair = Bip32ECKeyPair.generateKeyPair(MnemonicUtils.generateSeed(seedCode, ""))
    // Derive the keypair using the derivation path
    return Bip32ECKeyPair.deriveKeyPair(masterKeypair, derivationPath)
}
