package com.github.kklisura.cdt.protocol.v2023.events.css;

/*-
 * #%L
 * cdt-java-client
 * %%
 * Copyright (C) 2018 - 2023 Kenan Klisura
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.kklisura.cdt.protocol.v2023.support.annotations.Optional;
import com.github.kklisura.cdt.protocol.v2023.types.css.FontFace;

/**
 * Fires whenever a web font is updated. A non-empty font parameter indicates a successfully loaded
 * web font.
 */
public class FontsUpdated {

  @Optional
  private FontFace font;

  /** The web font that has loaded. */
  public FontFace getFont() {
    return font;
  }

  /** The web font that has loaded. */
  public void setFont(FontFace font) {
    this.font = font;
  }
}
