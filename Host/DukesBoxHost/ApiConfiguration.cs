using System.Linq;
using System.Web.Http;
using Autofac;
using Autofac.Integration.WebApi;
using Newtonsoft.Json;
using Newtonsoft.Json.Serialization;

namespace net.scottdukes.DukesBox
{
  public class ApiConfiguration : HttpConfiguration
  {
    public ApiConfiguration(ILifetimeScope container)
    {
      ConfigureDependencyResolver(container);
      ConfigureRoutes();
      ConfigureJsonSerialization();
      ConfgureDefaultResponseFormat();
    }

    private void ConfgureDefaultResponseFormat()
    {
      var appXmlType = this.Formatters.XmlFormatter.SupportedMediaTypes.FirstOrDefault( t => t.MediaType == "application/xml" );
      this.Formatters.XmlFormatter.SupportedMediaTypes.Remove( appXmlType );
    }

    private void ConfigureDependencyResolver(ILifetimeScope container)
    {
      var resolver = new AutofacWebApiDependencyResolver(container);
      DependencyResolver = resolver;
    }

    private void ConfigureRoutes()
    {
      Routes.MapHttpRoute(
        name: "ManifestApi",
        routeTemplate: "api/{tag}/{controller}"
        );
      Routes.MapHttpRoute(
        name: "DefaultApi",
        routeTemplate: "api/{controller}"
        );
    }

    private void ConfigureJsonSerialization()
    {
      var jsonSettings = Formatters.JsonFormatter.SerializerSettings;
      jsonSettings.Formatting = Formatting.Indented;
      jsonSettings.ContractResolver = new CamelCasePropertyNamesContractResolver();
    }
  }
}