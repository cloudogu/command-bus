/**
 * MIT License
 *
 * Copyright (c) 2017 TRIOLOGY GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.triology.cb.cdi;

import de.triology.cb.Command;
import de.triology.cb.CommandHandler;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry holds the mapping between a command and its handler.
 */
@Singleton
public class Registry {

  private Map<Class<? extends Command>, CommandProvider> providerMap = new HashMap<>();

  void register(Class<? extends Command> commandClass, CommandProvider provider){
    providerMap.put(commandClass, provider);
  }

  @SuppressWarnings("unchecked")
  <R, C extends Command<R>> CommandHandler<R,C> get(Class<C> commandClass) {
    return providerMap.get(commandClass).get();
  }

}
