using System;
using System.IO;
using System.Security.Cryptography;
using System.Text;

namespace net.scottdukes.DukesBox
{
  public static class CryptoHelper
  {
    [ThreadStatic]
    private static HashAlgorithm sHashAlgorithm;

    public static string ComputeHash( string content )
    {
      byte[] data = Encoding.ASCII.GetBytes( content );
      return ComputeHash( data );
    }

    private static HashAlgorithm HashAlgorithm
    {
      get
      {
        return sHashAlgorithm ?? ( sHashAlgorithm = new SHA1CryptoServiceProvider() );
      }
    }

    public static string ComputeHash( byte[] data )
    {
      byte[] hash = ComputeByteHash( data );
      return BitConverter.ToString( hash ).Replace( "-", string.Empty );
    }

    public static byte[] ComputeByteHash( byte[] data )
    {
      return HashAlgorithm.ComputeHash( data );
    }

    public static string ComputeFileHash( FileInfo file )
    {
      using (Stream stream = file.OpenRead())
      {
        byte[] hash = HashAlgorithm.ComputeHash( stream );
        return BitConverter.ToString(hash).Replace("-", string.Empty).ToLowerInvariant();
      }
    }
  }
}