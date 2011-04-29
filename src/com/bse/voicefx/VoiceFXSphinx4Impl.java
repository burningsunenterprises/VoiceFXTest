/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *  Copyright 2008-2011 Burning Sun Enterprises.
 *  All Rights Reserved.
 * 
 *  Use is subject to license terms.
 * 
 *  This file is available and licensed under the following license:
 * 
 *  Redistribution and use in source binary forms, without modification,
 *  is permitted provided that the following conditions are met:
 * 
 *  Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *  Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation
 *      and/or other materials provided with the distribution.
 * 
 *   The name of Burning Sun Enterprises may not be used to endorse or
 *      promote products derived from this software without specific prior
 *      written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 *  LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 *  A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 *  OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 *  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.bse.voicefx;

import com.sun.speech.engine.recognition.BaseRecognizer;
import com.sun.speech.engine.recognition.BaseRuleGrammar;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.jsgf.JSGFGrammarParseException;
import edu.cmu.sphinx.recognizer.Recognizer;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import java.io.IOException;
import javax.speech.recognition.GrammarException;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;

/**
 *
 * @author Eric M. Smith   Burning Sun Enterprises
 */
public class VoiceFXSphinx4Impl extends VoiceFXImpl {

  private ConfigurationManager configManager;
  private Recognizer recognizer;
  private Microphone mic;
  private RuleGrammar parseGrammar;
  private JSGFGrammar jsgfGrammar;
  private BaseRecognizer jsapiRecognizer;

  final private static String EMPTY_PARSE_STRING = "";

  public VoiceFXSphinx4Impl(VoiceFXListener listener, String configURL ) {
    super(listener);

    configManager = new ConfigurationManager(VoiceFXSphinx4Impl.class.getResource(configURL));
    if ( configManager == null ) {
      throw new ExceptionInInitializerError( "Unable to load configuration file." );
    }

    recognizer = (Recognizer) configManager.lookup("recognizer");
    if ( recognizer == null ) {
      throw new ExceptionInInitializerError( "Unable to load recognizer." );
    }

    recognizer.allocate();
    if ( recognizer == null ) {
      throw new ExceptionInInitializerError( "Unable to allocate recognizer." );
    }

    mic = (Microphone) configManager.lookup("microphone");
    if ( mic == null ) {
      throw new ExceptionInInitializerError( "Unable to create microphone." );
    }

    jsgfGrammar = (JSGFGrammar) configManager.lookup("jsgfGrammar");
    if ( jsgfGrammar == null ) {
      throw new ExceptionInInitializerError( "Unable to locate grammar." );
    }
    jsapiRecognizer = new BaseRecognizer(jsgfGrammar.getGrammarManager());
    parseGrammar = new BaseRuleGrammar(jsapiRecognizer, jsgfGrammar.getRuleGrammar());

    if (!mic.startRecording()) {
      recognizer.deallocate();
      throw new ExceptionInInitializerError( "Unable to activate the microphone." );
    }

    runnable = true;
  }

  @Override
  public void replaceGrammar(String grammarFileURL) throws VoiceFXException {
    try {
      jsgfGrammar.loadJSGF(grammarFileURL);
      parseGrammar = new BaseRuleGrammar(jsapiRecognizer, jsgfGrammar.getRuleGrammar());
    }
    catch (IOException e) {
      throw new VoiceFXException( new GrammarException(e.getMessage()) );
    }
    catch (JSGFGrammarException e) {
      throw new VoiceFXException( new GrammarException(e.getMessage()) );
    }
    catch (JSGFGrammarParseException e) {
      throw new VoiceFXException( new GrammarException(e.getMessage()) );
    }
  }


  @Override
  public void run() throws Exception {

    Result result;
    String bestResult;
    RuleParse parse;
    while ( runnable && !Thread.interrupted() ) {
      result = recognizer.recognize();
      if (result != null) {
        bestResult = result.getBestFinalResultNoFiller();
        parse = parseGrammar.parse(bestResult, null);
        if ( parse != null ) {
          notifyListener( bestResult, parse.toString() );
        }
        else {
          notifyListener( bestResult, EMPTY_PARSE_STRING );
        }
      }
    }
  }
}
