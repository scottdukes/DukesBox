using System;
using System.Diagnostics;
using System.IO;
using System.Reflection;
using System.Web.Http;
using System.Xml.Serialization;
using Autofac;
using Autofac.Integration.WebApi;
using log4net;
using log4net.Config;
using Topshelf;
using Topshelf.Autofac;

namespace net.scottdukes.DukesBox
{
  internal class Program
  {
    public static void Main()
    {
      ConfigureLog4Net();

      var config = ParseConfig();

      var builder = new ContainerBuilder();
      builder.RegisterInstance( config ).SingleInstance();
      builder.RegisterType<DukesBoxService>().SingleInstance();

      // Scan an assembly for components
      builder.RegisterApiControllers( Assembly.GetExecutingAssembly() );

      builder.RegisterType<ApiConfiguration>().As<HttpConfiguration>().SingleInstance();

      builder.RegisterType<ManifestCache>().SingleInstance();
      builder.RegisterType<ManifestBuilder>().SingleInstance();

      IContainer container = builder.Build();

      var host = HostFactory.New( x =>
      {
        x.UseLog4Net();

        // Pass it to Topshelf
        x.UseAutofacContainer( container );

        x.SetServiceName( "DukesBoxService" );
        x.SetDisplayName( "DukesBoxService" );
        x.SetDescription( "Host for DukesBox files." );

        x.Service<DukesBoxService>( s =>
        {
          // Let Topshelf use it
          s.ConstructUsingAutofacContainer();
          //s.ConstructUsing( name => new DukesBoxService( config ) );
          s.WhenStarted( tc => tc.Start() );
          s.WhenStopped( tc => tc.Stop() );
        } );
      } );

      host.Run();

      WaitForKeyPressIfDebugIsAttached();
    }

    private static SyncConfig ParseConfig()
    {
      string configPath = Path.Combine( AppDomain.CurrentDomain.BaseDirectory, "config.xml" );
      var serializer = new XmlSerializer( typeof( SyncConfig ) );
      using( var reader = File.OpenText( configPath ) )
      {
        return (SyncConfig) serializer.Deserialize( reader );
      }
    }

    private static void ConfigureLog4Net()
    {
      string logConfig = Path.Combine( AppDomain.CurrentDomain.BaseDirectory, "log4net.config" );
      XmlConfigurator.ConfigureAndWatch( new FileInfo( logConfig ) );
    }

    [Conditional( "DEBUG" )]
    private static void WaitForKeyPressIfDebugIsAttached()
    {
      if( Debugger.IsAttached )
      {
        Console.WriteLine( "Press any key to exit." );
        Console.ReadKey();
      }
    }
  }
}