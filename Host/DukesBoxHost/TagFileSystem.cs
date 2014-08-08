using System;
using System.Collections.Generic;
using Microsoft.Owin.FileSystems;

namespace net.scottdukes.DukesBox
{
  internal class TagFileSystem : IFileSystem
  {
    private readonly IDictionary<string, IFileSystem> mFileSystems = new Dictionary<string, IFileSystem>( StringComparer.OrdinalIgnoreCase );

    public bool TryGetFileInfo( string subpath, out IFileInfo fileInfo )
    {
      foreach( var tag in mFileSystems.Keys )
      {
        string actualSubPath;
        if( MatchesTag( subpath, tag, out actualSubPath ) )
        {
          bool result = mFileSystems[ tag ].TryGetFileInfo( actualSubPath, out fileInfo );
          return result;
        }
      }

      fileInfo = null;
      return false;
    }

    public bool TryGetDirectoryContents( string subpath, out IEnumerable<IFileInfo> contents )
    {
      foreach( var tag in mFileSystems.Keys )
      {
        string actualSubPath;
        if( MatchesTag( subpath, tag, out actualSubPath ) )
        {
          bool result = mFileSystems[ tag ].TryGetDirectoryContents( actualSubPath, out contents );
          return result;
        }
      }

      contents = null;
      return false;
    }

    public void AddTagRoot(string tag, string root)
    {
      if (!mFileSystems.ContainsKey(tag))
        mFileSystems[tag] = CreateFileSystem( root );
    }

    internal Func<string, IFileSystem> CreateFileSystem = root => new PhysicalFileSystem( root );

    internal bool MatchesTag(string subPath, string tag, out string actualSubPath)
    {
      var prefix = "/" + tag + "/";
      if( subPath.StartsWith( prefix ) )
      {
        actualSubPath = "/" + subPath.After( prefix );
        return true;
      }

      actualSubPath = null;
      return false;
    }
  }
}