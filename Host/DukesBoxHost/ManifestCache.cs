//using System.Threading;
using System;
using System.Linq;
using System.Runtime.Caching;
using System.Threading.Tasks;
using System.Timers;
using log4net;

namespace net.scottdukes.DukesBox
{
  public class ManifestCache
  {
    private readonly MemoryCache mCache = MemoryCache.Default;

    private static readonly ILog Log = LogManager.GetLogger( typeof( ManifestCache ) );
    private readonly SyncConfig mConfig;
    private readonly ManifestBuilder mManifestBuilder;
    private readonly Timer mTimer = new Timer();

    public ManifestCache( SyncConfig config )
    {
      mConfig = config;
      mManifestBuilder = new ManifestBuilder( config );
      mTimer.Interval = TimeSpan.FromSeconds( 10 ).TotalMilliseconds;
      mTimer.Elapsed += delegate
      {
        LoadAll();
      };
    }

    public void LoadAll()
    {
      Parallel.ForEach( mConfig.Directories.Select( x => x.Tag ), UpdateTag );
    }

    private void UpdateTag( string tag )
    {
      var policy = new CacheItemPolicy
      {
        Priority = CacheItemPriority.Default
      };
      VManifest manifest = Load( tag );
      mCache.Set( tag, manifest, policy );
      Log.TraceFormat("Updated manifest for {0} (count: {1})", tag, manifest.Files.Count);
    }

    public VManifest Get( string tag )
    {
      if( !mCache.Contains( tag ) )
      {
        UpdateTag( tag );
      }

      return (VManifest) mCache.Get( tag );
    }

    public VManifest Load( string tag )
    {
      SyncDir dir = LookupDir( tag );
      if( dir == null ) return new VManifest( mConfig.BaseFileUri, tag );

      Log.VerboseFormat( "Building manifest for {0}", dir );
      return mManifestBuilder.BuildFor( dir );
    }

    private SyncDir LookupDir( string tag )
    {
      return mConfig.Directories.FirstOrDefault( x => x.Tag.Equals( tag, StringComparison.OrdinalIgnoreCase ) );
    }

    public void Start()
    {
      LoadAll();
      mTimer.Start();
    }

    public void Stop()
    {
      mTimer.Stop();
    }
  }
}