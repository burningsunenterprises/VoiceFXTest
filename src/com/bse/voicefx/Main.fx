/*
 *  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *  Copyright 2008-2010 Burning Sun Enterprises.
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

import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.async.RunnableFuture;

/**
 * @author e033510
 */

var textContents: String;

var contentText: Text = Text {
  font: Font {
    size: 16
  }
  x: 10
  y: 30
  content: bind textContents
};


Stage {
    title: "Application title"
    scene: Scene {
        width: 250
        height: 80
        content: [
          contentText
        ]
    }
}

function replaceText(token: String, grammar:String): Void {
  textContents = token;
}


function createListener(): RunnableFuture {
  new VoiceFXSphinx4Impl( voicefx, "voicefx.testconfig.xml" );
}

var voicefx: VoiceFX = VoiceFX {
  createListener: createListener
  onReceive: replaceText
}

voicefx.start();
