using System;
using System.Collections.Generic;
using log4net;
using Microsoft.Owin.StaticFiles.ContentTypes;

namespace net.scottdukes.DukesBox
{
  internal class ExtensionContentTypeProvider : IContentTypeProvider
  {
    private static readonly ILog Log = LogManager.GetLogger(typeof(ExtensionContentTypeProvider));
    
    private readonly IContentTypeProvider mDefaultContentTypeProvider = new FileExtensionContentTypeProvider();

    private readonly Dictionary<string, string> mMappings = new Dictionary<string, string>
    {
      { "json", "application/json" },
      { "jbc", "application/json" },
      { "ta", "application/json" },
      { "mustache", "text/plain" }
    };

    /// <summary>
    /// Maps an extension to a content type.
    /// </summary>
    /// <param name="extension">The extension to map, without a leading dot.</param>
    /// <param name="contentType">The content type to map to the extension.</param>
    public void AddMapping( string extension, string contentType )
    {
      if (!mMappings.ContainsKey(extension))
      mMappings.Add(extension, contentType);
    }

    public bool TryGetContentType( string subpath, out string contentType )
    {
      string extension = subpath.AfterLast(".");

      if( mMappings.TryGetValue( extension.ToLowerInvariant(), out contentType ) )
      {
        return true;
      }

      if( mDefaultContentTypeProvider.TryGetContentType( subpath, out contentType ) )
      {
        return true;
      }

      Log.WarnFormat("Unknown file type. {0}", subpath);
      contentType = null;
      return false;
    }
  }
}