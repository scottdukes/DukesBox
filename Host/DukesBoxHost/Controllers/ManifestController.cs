using System.Net;
using System.Web.Http;
using log4net;

namespace net.scottdukes.DukesBox.Controllers
{
  public class ManifestController : ApiController
  {
    private static readonly ILog Log = LogManager.GetLogger( typeof( ManifestController ) );

    private readonly ManifestCache mCache;

    public ManifestController( ManifestCache cache )
    {
      mCache = cache;
    }

    public VManifest Get( string tag )
    {
      var manifest = mCache.Get( tag );
      Log.TraceFormat("Got manifest for {0} containing {1} files.", tag, manifest.Files.Count);

      if( manifest == null )
        throw new HttpResponseException( HttpStatusCode.NotFound );

      return manifest;
    }

    public VManifest Post( string tag, VManifest remoteManifest )
    {
      Log.TraceFormat( "Got remote manifest for {0} containing {1} files.", tag, remoteManifest.Files.Count );
      var localManifest = Get( tag );

      VManifest deltaManifest = localManifest.DifferenceFrom( remoteManifest );
      Log.InfoFormat( "[{0}] delta: {1} files.", tag, deltaManifest.Files.Count );

      foreach( var file in deltaManifest.Files )
      {
        Log.DebugFormat("[{1}] Syncing {0}", file.Path, tag);
      }

      return deltaManifest;
    }
  }
}