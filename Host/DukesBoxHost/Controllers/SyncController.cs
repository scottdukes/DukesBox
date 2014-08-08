using System;
using System.Collections.Generic;
using System.Web.Http;
using log4net;

namespace net.scottdukes.DukesBox.Controllers
{
  public class SyncController : ApiController
  {
    private static readonly ILog Log = LogManager.GetLogger(typeof(ManifestController));

    private readonly SyncConfig mConfig;

    public SyncController( SyncConfig config )
    {
      mConfig = config;
    }

    public SyncViewModel Get()
    {
      Log.DebugFormat("GET request for sync context.");
      return SyncViewModel.Build( mConfig );
    }
  }

  public class SyncViewModel
  {
    public SyncViewModel()
    {
      Dirs = new List<SyncDirModel>();
    }

    public List<SyncDirModel> Dirs { get; set; }

    public static SyncViewModel Build( SyncConfig config )
    {
      var model = new SyncViewModel();

      foreach( var directory in config.Directories )
      {
        model.Dirs.Add( new SyncDirModel
        {
          Tag = directory.Tag,
          Local = directory.RemotePath,
          Manifest = new Uri( config.BaseUri, string.Format( "api/{0}/manifest", directory.Tag ) ).AbsoluteUri
        } );
      }

      return model;
    }


    public class SyncDirModel
    {
      public string Tag { get; set; }
      public string Local { get; set; }
      public string Manifest { get; set; }
    }
  }
}