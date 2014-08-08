using System;
using System.IO;
using System.Linq;
using System.Linq.Expressions;

namespace net.scottdukes.DukesBox
{
  public class ManifestBuilder
  {
    private readonly SyncConfig mConfig;

    public ManifestBuilder( SyncConfig config )
    {
      mConfig = config;
    }

    public VManifest BuildFor( SyncDir dir )
    {
      var baseDir = new DirectoryInfo( dir.LocalPath );
      if( !baseDir.Exists )
        throw new DirectoryNotFoundException( "Could not find " + baseDir.FullName );


      var manifest = new VManifest( mConfig.BaseFileUri, dir.Tag );

      var finder = new FileFinder(dir.LocalPath, dir.Depth);

      var files = finder.FindFiles("*.*").AsParallel()
        .Select( x => BuildVFile( baseDir, new FileInfo(x) ) )
        .OrderBy(x => x.Path);

      manifest.Files.AddRange( files );

      return manifest;
    }

    private static VFile BuildVFile( DirectoryInfo baseDir, FileInfo file )
    {
      var prefix = baseDir.FullName + Path.DirectorySeparatorChar;
      var relativePath = file.FullName.After( prefix ).NormalizePathForWeb();

      var vFile = new VFile
      {
        Path = relativePath,
        Size = file.Length,
        Hash = CryptoHelper.ComputeFileHash( file )
      };

      return vFile;
    }
  }
}