package ch.ehi.basics.io;


import java.io.Writer;
import java.io.IOException;
import java.io.InterruptedIOException;


/** @author Sascha Brawer
*/
public class IndentPrintWriter extends Writer {
  protected Writer   out;
  private int        indent = 0;
  private int        col;
  private String     lineSeparator;
  private boolean    trouble = false;

  public IndentPrintWriter(Writer out) {
    super(out);
    this.out = out;
    lineSeparator = System.getProperty("line.separator");
    col = 0;
  }

  public void indent() {
    indent = indent + 1;
  }

  public void unindent() {
    if (indent > 0)
      indent = indent - 1;
  }



  private void ensureOpen() throws IOException
  {
    if (out == null)
      throw new IOException("Stream closed");
  }


  /** Check to make sure that the stream has not been closed */
  private void willWrite() throws IOException
  {
    ensureOpen();
    if (col == 0)
    {
      col = 2 * indent;
      for (int i = 0; i < col; i++)
        out.write(' ');
    }
  }


  /** Flush the stream. */
  public void flush() {
	try {
      synchronized (lock) {
        if (out == null)
          throw new IOException("Stream closed");
        out.flush();
	  }
	} catch (IOException x) {
      trouble = true;
	}
  }


  /** Close the stream. */
  public void close() {
	try {
	    synchronized (lock) {
		if (out == null)
		    return;
		out.close();
		out = null;
	    }
	}
	catch (IOException x) {
	    trouble = true;
	}
    }


    /**
     * Flush the stream and check its error state.  Errors are cumulative;
     * once the stream encounters an error, this routine will return true on
     * all successive calls.
     *
     * @return True if the print stream has encountered an error, either on the
     * underlying output stream or during a format conversion.
     */
    public boolean checkError() {
	if (out != null)
	    flush();
	return trouble;
    }


  /** Indicate that an error has occurred. */
  protected void setError() {
    trouble = true;
  }




    /*
     * Exception-catching, synchronized output operations,
     * which also implement the write() methods of Writer
     */


  /** Write a single character. */
  public void write(int c) {
    try {
      synchronized (lock) {
        willWrite();
        out.write(c);
        col = col + 1;
	  }
	} catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
	} catch (IOException x) {
      trouble = true;
	}
  }


  /** Write a portion of an array of characters. */
  public void write(char buf[], int off, int len) {
    try {
      synchronized (lock) {
        willWrite();
        out.write(buf, off, len);
        col += len;
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
	} catch (IOException x) {
	  trouble = true;
	}
  }



  /** Write an array of characters.
  */
  public void write(char buf[]) {
    write(buf, 0, buf.length);
  }


  /** Write a portion of a string. */
  public void write(String s, int off, int len) {
	try {
      synchronized (lock) {
        willWrite();
        out.write(s, off, len);
        col += len;
      }
    } catch (InterruptedIOException x) {
      Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
	}
  }


  /**
   * Write a string.  This method cannot be inherited from the Writer class
   * because it must suppress I/O exceptions.
   */
  public void write(String s) {
    write(s, 0, s.length());
  }


  private void newLine() {
    try {
      synchronized (lock) {
        ensureOpen();
        out.write(lineSeparator);
        col = 0;
      }
    } catch (InterruptedIOException x) {
     Thread.currentThread().interrupt();
    } catch (IOException x) {
      trouble = true;
    }
  }


  /* Methods that do not terminate lines */


    /**
     * Print a boolean value.  The string produced by <code>{@link
     * java.lang.String#valueOf(boolean)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      b   The <code>boolean</code> to be printed
     */
    public void print(boolean b) {
	write(b ? "true" : "false");
    }


    /**
     * Print a character.  The character is translated into one or more bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      c   The <code>char</code> to be printed
     */
    public void print(char c) {
	write(String.valueOf(c));
    }


    /**
     * Print an integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(int)}</code> is translated into bytes according
     * to the platform's default character encoding, and these bytes are
     * written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      i   The <code>int</code> to be printed
     * @see        java.lang.Integer#toString(int)
     */
    public void print(int i) {
	write(String.valueOf(i));
    }


    /**
     * Print a long integer.  The string produced by <code>{@link
     * java.lang.String#valueOf(long)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      l   The <code>long</code> to be printed
     * @see        java.lang.Long#toString(long)
     */
    public void print(long l) {
	write(String.valueOf(l));
    }


    /**
     * Print a floating-point number.  The string produced by <code>{@link
     * java.lang.String#valueOf(float)}</code> is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      f   The <code>float</code> to be printed
     * @see        java.lang.Float#toString(float)
     */
    public void print(float f) {
	write(String.valueOf(f));
    }


    /**
     * Print a double-precision floating-point number.  The string produced by
     * <code>{@link java.lang.String#valueOf(double)}</code> is translated into
     * bytes according to the platform's default character encoding, and these
     * bytes are written in exactly the manner of the <code>{@link
     * #write(int)}</code> method.
     *
     * @param      d   The <code>double</code> to be printed
     * @see        java.lang.Double#toString(double)
     */
    public void print(double d) {
	write(String.valueOf(d));
    }


    /**
     * Print an array of characters.  The characters are converted into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      s   The array of chars to be printed
     *
     * @throws  NullPointerException  If <code>s</code> is <code>null</code>
     */
    public void print(char s[]) {
	write(s);
    }


    /**
     * Print a string.  If the argument is <code>null</code> then the string
     * <code>"null"</code> is printed.  Otherwise, the string's characters are
     * converted into bytes according to the platform's default character
     * encoding, and these bytes are written in exactly the manner of the
     * <code>{@link #write(int)}</code> method.
     *
     * @param      s   The <code>String</code> to be printed
     */
    public void print(String s) {
	if (s == null) {
	    s = "null";
	}
	write(s);
    }


    /**
     * Print an object.  The string produced by the <code>{@link
     * java.lang.String#valueOf(Object)}</code> method is translated into bytes
     * according to the platform's default character encoding, and these bytes
     * are written in exactly the manner of the <code>{@link #write(int)}</code>
     * method.
     *
     * @param      obj   The <code>Object</code> to be printed
     * @see        java.lang.Object#toString()
     */
    public void print(Object obj) {
	write(String.valueOf(obj));
    }




    /* Methods that do terminate lines */


    /**
     * Terminate the current line by writing the line separator string.  The
     * line separator string is defined by the system property
     * <code>line.separator</code>, and is not necessarily a single newline
     * character (<code>'\n'</code>).
     */
    public void println() {
	newLine();
    }


    /**
     * Print a boolean value and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(boolean)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(boolean x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print a character and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(char)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public void println(char x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print an integer and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(int)}</code> and then <code>{@link
     * #println()}</code>.
     */
    public void println(int x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print a long integer and then terminate the line.  This method behaves
     * as though it invokes <code>{@link #print(long)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(long x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print a floating-point number and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(float)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(float x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print a double-precision floating-point number and then terminate the
     * line.  This method behaves as though it invokes <code>{@link
     * #print(double)}</code> and then <code>{@link #println()}</code>.
     */
    public void println(double x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print an array of characters and then terminate the line.  This method
     * behaves as though it invokes <code>{@link #print(char[])}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(char x[]) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print a String and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(String)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(String x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }


    /**
     * Print an Object and then terminate the line.  This method behaves as
     * though it invokes <code>{@link #print(Object)}</code> and then
     * <code>{@link #println()}</code>.
     */
    public void println(Object x) {
	synchronized (lock) {
	    print(x);
	    println();
	}
    }
}
