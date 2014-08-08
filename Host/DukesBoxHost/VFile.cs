using System;
using System.Diagnostics;
using System.Runtime.Serialization;

namespace net.scottdukes.DukesBox
{
  [DebuggerDisplay("{Path}")]
  public class VFile : IEquatable<VFile>
  {
    [DataMember]
    public string Path { get; set; }
    [DataMember]
    public string Hash { get; set; }
    [DataMember]
    public long Size { get; set; }

    public bool Equals( VFile other )
    {
      if( ReferenceEquals( null, other ) ) return false;
      if( ReferenceEquals( this, other ) ) return true;
      return string.Equals( Path, other.Path ) && string.Equals( Hash, other.Hash ) && Size == other.Size;
    }

    public override bool Equals( object obj )
    {
      if( ReferenceEquals( null, obj ) ) return false;
      if( ReferenceEquals( this, obj ) ) return true;
      if( obj.GetType() != this.GetType() ) return false;
      return Equals( (VFile) obj );
    }

    public override int GetHashCode()
    {
      unchecked
      {
        var hashCode = ( Path != null ? Path.GetHashCode() : 0 );
        hashCode = ( hashCode * 397 ) ^ ( Hash != null ? Hash.GetHashCode() : 0 );
        hashCode = ( hashCode * 397 ) ^ Size.GetHashCode();
        return hashCode;
      }
    }

    public static bool operator ==( VFile left, VFile right )
    {
      return Equals( left, right );
    }

    public static bool operator !=( VFile left, VFile right )
    {
      return !Equals( left, right );
    }
  }

}