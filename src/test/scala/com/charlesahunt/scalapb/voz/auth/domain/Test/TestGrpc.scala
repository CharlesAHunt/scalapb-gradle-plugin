package voz.auth.domain.Test

object TestGrpc {
  val METHOD_TEST: _root_.io.grpc.MethodDescriptor[voz.auth.domain.Test.TestRequest, voz.auth.domain.Test.TestReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("voz.auth.domain.Test", "Test"))
      .setRequestMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Test.TestRequest))
      .setResponseMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Test.TestReply))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("voz.auth.domain.Test")
      .setSchemaDescriptor(new _root_.com.trueaccord.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(voz.auth.domain.Test.TestProto.javaDescriptor))
      .addMethod(METHOD_TEST)
      .build()
  
  trait Test extends _root_.com.trueaccord.scalapb.grpc.AbstractService {
    override def serviceCompanion = Test
    def test(request: voz.auth.domain.Test.TestRequest): scala.concurrent.Future[voz.auth.domain.Test.TestReply]
  }
  
  object Test extends _root_.com.trueaccord.scalapb.grpc.ServiceCompanion[Test] {
    implicit def serviceCompanion: _root_.com.trueaccord.scalapb.grpc.ServiceCompanion[Test] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = voz.auth.domain.Test.TestProto.javaDescriptor.getServices().get(0)
  }
  
  trait TestBlockingClient {
    def serviceCompanion = Test
    def test(request: voz.auth.domain.Test.TestRequest): voz.auth.domain.Test.TestReply
  }
  
  class TestBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[TestBlockingStub](channel, options) with TestBlockingClient {
    override def test(request: voz.auth.domain.Test.TestRequest): voz.auth.domain.Test.TestReply = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_TEST, options), request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): TestBlockingStub = new TestBlockingStub(channel, options)
  }
  
  class TestStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[TestStub](channel, options) with Test {
    override def test(request: voz.auth.domain.Test.TestRequest): scala.concurrent.Future[voz.auth.domain.Test.TestReply] = {
      com.trueaccord.scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_TEST, options), request))
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): TestStub = new TestStub(channel, options)
  }
  
  def bindService(serviceImpl: Test, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_TEST,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[voz.auth.domain.Test.TestRequest, voz.auth.domain.Test.TestReply] {
        override def invoke(request: voz.auth.domain.Test.TestRequest, observer: _root_.io.grpc.stub.StreamObserver[voz.auth.domain.Test.TestReply]): Unit =
          serviceImpl.test(request).onComplete(com.trueaccord.scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): TestBlockingStub = new TestBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): TestStub = new TestStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = voz.auth.domain.Test.TestProto.javaDescriptor.getServices().get(0)
  
}