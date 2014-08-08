using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Runtime.Serialization;
using System.Threading.Tasks;
using System.Xml.Serialization;

namespace net.scottdukes.DukesBox
{
  [XmlRoot(ElementName = "syncroots")]
  public class SyncConfig
  {
    private Lazy<Uri> mBaseUri;

    public SyncConfig()
    {
      mBaseUri = new Lazy<Uri>( BuildHostUri );
    }

    [XmlAttribute("port")]
    public int Port { get; set; }

    [XmlElement(ElementName = "syncdir")]
    public List<SyncDir> Directories { get; set; }

    public Uri BaseUri
    {
      get { return mBaseUri.Value; } 
    }

    public Uri BaseFileUri
    {
      get { return new Uri(BaseUri, "file"); } 
    }


    public Uri BuildHostUri()
    {
      var machineName = Environment.MachineName;
      var hostEntry = Dns.GetHostEntry(machineName);
      var ipAddress = hostEntry.AddressList.First(x => x.AddressFamily == AddressFamily.InterNetwork);

      var builder = new UriBuilder
      {
        Host = ipAddress.ToString(),
        Port = Port
      };
      return builder.Uri;
    }
  }

  [DebuggerDisplay("{Tag} {LocalPath} => {RemotePath}")]
  [DataContract]
  public class SyncDir
  {
    public SyncDir( )
    {
      Depth = 20;
    }

    [DataMember]
    [XmlAttribute("tag")]
    public string Tag { get; set; }

    [XmlAttribute("local")]
    public string LocalPath { get; set; }

    [DataMember]
    [XmlAttribute("remote")]
    public string RemotePath { get; set; }

    [XmlAttribute("depth")]
    public int Depth { get; set; }

    [XmlAttribute("ignore")]
    public bool Ignore { get; set; }

    public override string ToString()
    {
      return string.Format( "[{0}] {1} (depth: {2})", Tag, LocalPath, Depth );
    }
  }
}