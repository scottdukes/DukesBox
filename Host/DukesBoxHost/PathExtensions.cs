using System.Globalization;
using System.IO;

namespace net.scottdukes.DukesBox
{
  public static class PathExtensions
  {
    public static string NormalizePathForWeb( this string path )
    {
      return path.Replace( '\\', '/' );
    }

    public static string NormalizePathForWindows( this string path )
    {
      return path.Replace( '/', '\\' );
    }

    public static string AppendTrailingSlash( this string path )
    {
      if( path.EndsWith( Path.DirectorySeparatorChar.ToString( CultureInfo.InvariantCulture ) ) )
        return path;

      return path + Path.DirectorySeparatorChar;
    }
  }
}