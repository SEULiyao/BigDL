/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.bigdl.ppml.attestation.verifier

import com.intel.analytics.bigdl.ppml.dcap.Attestation
import org.apache.logging.log4j.LogManager

import com.intel.analytics.bigdl.ppml.attestation._

/**
 * Verify SGX quote with SGX SDK
 */
class SGXDCAPQuoteVerifierImpl extends QuoteVerifier with Serializable {
  val logger = LogManager.getLogger(getClass)

  @throws(classOf[AttestationRuntimeException])
  override def verifyQuote(quote: Array[Byte]): Int = {

    try {
      val verifyQuoteResult = Attestation.sdkVerifyQuote(quote)
      val debug = System.getenv("ATTESTATION_DEBUG")
      if (verifyQuoteResult == 0) {
        System.out.println("INFO: Quote Verification Success!")
      } else if (verifyQuoteResult == 1) {
        System.out.println("WARNING: Quote verification passed but BIOS or the software" +
          " is not up to date.")
      } else if (debug == "true") {
        System.out.println("ERROR: Quote Verification Fail! In debug mode, continue.")
      }
      else {
        System.out.println("ERROR: Quote Verification Fail! Application killed.")
        throw new AttestationRuntimeException("Quote Verification Fail!")
      }

      return verifyQuoteResult
    } catch {
      case e: Exception =>
        logger.error(s"Failed to verify quote, ${e}")
        throw new AttestationRuntimeException("Failed " +
          "to verify quote", e)
    }

    throw new AttestationRuntimeException("Unexpected workflow when verifying Quote!")
  }
}
