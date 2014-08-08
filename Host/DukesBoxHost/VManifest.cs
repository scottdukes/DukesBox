using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;

namespace net.scottdukes.DukesBox
{
  [DataContract]
  public class VManifest
  {
    public VManifest()
    {
      Files = new List<VFile>();
    }

    public VManifest(Uri baseFileUri, string tag) : this()
    {
      BaseUri = baseFileUri.AbsoluteUri + "/" + tag;
    }

    [DataMember]
    public string BaseUri { get; set; }

    [DataMember]
    public List<VFile> Files { get; set; }

    public VManifest DifferenceFrom( VManifest other )
    {
      var localLookup = this.Files.ToDictionary( x => x.Path );
      var remoteLookup = other.Files.ToDictionary( x => x.Path );

      var delta = new VManifest
      {
        BaseUri = this.BaseUri
      };

      foreach( var file in this.Files )
      {
        VFile otherFile;
        if( !remoteLookup.TryGetValue( file.Path, out otherFile ) )
        {
          delta.Files.Add(file);
        }
        else if (file != otherFile)
        {
          delta.Files.Add(file);
        }
      }


      return delta;
    }

  }
}