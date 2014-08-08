using net.scottdukes.DukesBox;
using FluentAssertions;
using Microsoft.Owin.FileSystems;
using NUnit.Framework;
using Rhino.Mocks;

namespace net.scottdukes.DukesBox.Tests
{
  public class TagFileSystemTests
  {
    private TagFileSystem sut;

    [SetUp]
    public void CreateSUT()
    {
      sut = new TagFileSystem
      {
        CreateFileSystem = _ => MockRepository.GenerateStub<IFileSystem>()
      };

      sut.AddTagRoot( "bar", "foo\\bar" );
      sut.AddTagRoot( "www", "foo\\www" );
      sut.AddTagRoot( "www2", "foo\\www2" );
    }


    [Test]
    public void MatchesTagShouldMatch()
    {
      string actualSubPath;
      sut.MatchesTag( "/bar/image.png", "bar", out actualSubPath ).Should().BeTrue();
      actualSubPath.Should().Be( "/image.png" );
    }

    [Test]
    public void MatchesTagShouldNotMatchSimilarTag()
    {
      string actualSubPath;
      sut.MatchesTag( "/www2/image.png", "www", out actualSubPath ).Should().BeFalse();
      sut.MatchesTag( "/www2/image.png", "www2", out actualSubPath ).Should().BeTrue();
      actualSubPath.Should().Be( "/image.png" );
    }
  }
}