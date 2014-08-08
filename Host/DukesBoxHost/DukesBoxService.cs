using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Security.AccessControl;
using System.Web.Http;
using log4net;
using Microsoft.Owin;
using Microsoft.Owin.FileSystems;
using Microsoft.Owin.Hosting;
using Microsoft.Owin.StaticFiles;
using Owin;

namespace net.scottdukes.DukesBox
{
  public class DukesBoxService
  {
    private static readonly ILog Log = LogManager.GetLogger( typeof( DukesBoxService ) );
    private readonly SyncConfig mConfig;
    private readonly HttpConfiguration mHttpConfiguration;
    private readonly ManifestCache mManifestCache;
    private IFileSystem mFileSystem = new TagFileSystem();
    private IDisposable mWebApp;

    public DukesBoxService( SyncConfig config, HttpConfiguration httpConfiguration, ManifestCache manifestCache )
    {
      mConfig = config;
      mHttpConfiguration = httpConfiguration;
      mManifestCache = manifestCache;
    }

    public void Start()
    {
      try
      {
        var syncDirs = ExcludeMissingDirectories( mConfig.Directories );
        mFileSystem = BuildTagFileSystem( syncDirs );

        mWebApp = WebApp.Start( mConfig.BaseUri.AbsoluteUri, ConfigureWebApp );
        Log.InfoFormat("Listening on {0}", mConfig.BaseUri);

        foreach( var dir in syncDirs )
        {
          Log.DebugFormat("Hosting [{0}] {1} (depth: {3}) => {2}", dir.Tag, dir.LocalPath, dir.RemotePath, dir.Depth);
        }

        mManifestCache.Start();

      }
      catch( Exception )
      {
        mWebApp = null;
      }
    }

    private List<SyncDir> ExcludeMissingDirectories( IEnumerable<SyncDir> syncDirs )
    {
      return syncDirs.WhereNot(x => x.Ignore)
        .Where( x =>
      {
        if( !Directory.Exists( x.LocalPath ) )
        {
          Log.WarnFormat("Skipping [{0}] {1} as it does not exist.", x.Tag, x.LocalPath);
          return false;
        }

        return true;
      }).ToList();
    }

    public void Stop()
    {
      mManifestCache.Stop();
      if( mWebApp != null )
        mWebApp.Dispose();
    }


    private TagFileSystem BuildTagFileSystem( IEnumerable<SyncDir> syncDirs )
    {
      var fileSystem = new TagFileSystem();
      foreach( var syncDir in syncDirs )
      {
        fileSystem.AddTagRoot( syncDir.Tag, syncDir.LocalPath );
      }
      return fileSystem;
    }

    private void ConfigureWebApp(IAppBuilder app)
    {
      //app.UseWelcomePage();
      app.UseWebApi( mHttpConfiguration );

      var options = new FileServerOptions
      {
        RequestPath = new PathString( "/file" ),
        FileSystem = mFileSystem,
        EnableDirectoryBrowsing = true,
      };

      options.StaticFileOptions.ServeUnknownFileTypes = true;
      options.StaticFileOptions.ContentTypeProvider = new ExtensionContentTypeProvider();

      app.UseFileServer( options );
    }
  }
}