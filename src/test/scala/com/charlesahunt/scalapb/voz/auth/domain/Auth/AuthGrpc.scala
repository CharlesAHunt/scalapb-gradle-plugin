package voz.auth.domain.Auth

object AuthGrpc {
  val METHOD_GET_CHILD_TOKEN: _root_.io.grpc.MethodDescriptor[voz.auth.domain.Auth.ChildTokenRequest, voz.auth.domain.Auth.TokenReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("voz.auth.domain.Auth", "GetChildToken"))
      .setRequestMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.ChildTokenRequest))
      .setResponseMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.TokenReply))
      .build()
  
  val METHOD_GET_PARENT_TOKEN: _root_.io.grpc.MethodDescriptor[voz.auth.domain.Auth.TokenRequest, voz.auth.domain.Auth.TokenReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("voz.auth.domain.Auth", "GetParentToken"))
      .setRequestMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.TokenRequest))
      .setResponseMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.TokenReply))
      .build()
  
  val METHOD_GET_TOKEN: _root_.io.grpc.MethodDescriptor[voz.auth.domain.Auth.TokenRequest, voz.auth.domain.Auth.TokenReply] =
    _root_.io.grpc.MethodDescriptor.newBuilder()
      .setType(_root_.io.grpc.MethodDescriptor.MethodType.UNARY)
      .setFullMethodName(_root_.io.grpc.MethodDescriptor.generateFullMethodName("voz.auth.domain.Auth", "GetToken"))
      .setRequestMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.TokenRequest))
      .setResponseMarshaller(new com.trueaccord.scalapb.grpc.Marshaller(voz.auth.domain.Auth.TokenReply))
      .build()
  
  val SERVICE: _root_.io.grpc.ServiceDescriptor =
    _root_.io.grpc.ServiceDescriptor.newBuilder("voz.auth.domain.Auth")
      .setSchemaDescriptor(new _root_.com.trueaccord.scalapb.grpc.ConcreteProtoFileDescriptorSupplier(voz.auth.domain.Auth.AuthProto.javaDescriptor))
      .addMethod(METHOD_GET_CHILD_TOKEN)
      .addMethod(METHOD_GET_PARENT_TOKEN)
      .addMethod(METHOD_GET_TOKEN)
      .build()
  
  trait Auth extends _root_.com.trueaccord.scalapb.grpc.AbstractService {
    override def serviceCompanion = Auth
    def getChildToken(request: voz.auth.domain.Auth.ChildTokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply]
    def getParentToken(request: voz.auth.domain.Auth.TokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply]
    def getToken(request: voz.auth.domain.Auth.TokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply]
  }
  
  object Auth extends _root_.com.trueaccord.scalapb.grpc.ServiceCompanion[Auth] {
    implicit def serviceCompanion: _root_.com.trueaccord.scalapb.grpc.ServiceCompanion[Auth] = this
    def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = voz.auth.domain.Auth.AuthProto.javaDescriptor.getServices().get(0)
  }
  
  trait AuthBlockingClient {
    def serviceCompanion = Auth
    def getChildToken(request: voz.auth.domain.Auth.ChildTokenRequest): voz.auth.domain.Auth.TokenReply
    def getParentToken(request: voz.auth.domain.Auth.TokenRequest): voz.auth.domain.Auth.TokenReply
    def getToken(request: voz.auth.domain.Auth.TokenRequest): voz.auth.domain.Auth.TokenReply
  }
  
  class AuthBlockingStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[AuthBlockingStub](channel, options) with AuthBlockingClient {
    override def getChildToken(request: voz.auth.domain.Auth.ChildTokenRequest): voz.auth.domain.Auth.TokenReply = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_GET_CHILD_TOKEN, options), request)
    }
    
    override def getParentToken(request: voz.auth.domain.Auth.TokenRequest): voz.auth.domain.Auth.TokenReply = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_GET_PARENT_TOKEN, options), request)
    }
    
    override def getToken(request: voz.auth.domain.Auth.TokenRequest): voz.auth.domain.Auth.TokenReply = {
      _root_.io.grpc.stub.ClientCalls.blockingUnaryCall(channel.newCall(METHOD_GET_TOKEN, options), request)
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): AuthBlockingStub = new AuthBlockingStub(channel, options)
  }
  
  class AuthStub(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions = _root_.io.grpc.CallOptions.DEFAULT) extends _root_.io.grpc.stub.AbstractStub[AuthStub](channel, options) with Auth {
    override def getChildToken(request: voz.auth.domain.Auth.ChildTokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply] = {
      com.trueaccord.scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_GET_CHILD_TOKEN, options), request))
    }
    
    override def getParentToken(request: voz.auth.domain.Auth.TokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply] = {
      com.trueaccord.scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_GET_PARENT_TOKEN, options), request))
    }
    
    override def getToken(request: voz.auth.domain.Auth.TokenRequest): scala.concurrent.Future[voz.auth.domain.Auth.TokenReply] = {
      com.trueaccord.scalapb.grpc.Grpc.guavaFuture2ScalaFuture(_root_.io.grpc.stub.ClientCalls.futureUnaryCall(channel.newCall(METHOD_GET_TOKEN, options), request))
    }
    
    override def build(channel: _root_.io.grpc.Channel, options: _root_.io.grpc.CallOptions): AuthStub = new AuthStub(channel, options)
  }
  
  def bindService(serviceImpl: Auth, executionContext: scala.concurrent.ExecutionContext): _root_.io.grpc.ServerServiceDefinition =
    _root_.io.grpc.ServerServiceDefinition.builder(SERVICE)
    .addMethod(
      METHOD_GET_CHILD_TOKEN,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[voz.auth.domain.Auth.ChildTokenRequest, voz.auth.domain.Auth.TokenReply] {
        override def invoke(request: voz.auth.domain.Auth.ChildTokenRequest, observer: _root_.io.grpc.stub.StreamObserver[voz.auth.domain.Auth.TokenReply]): Unit =
          serviceImpl.getChildToken(request).onComplete(com.trueaccord.scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .addMethod(
      METHOD_GET_PARENT_TOKEN,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[voz.auth.domain.Auth.TokenRequest, voz.auth.domain.Auth.TokenReply] {
        override def invoke(request: voz.auth.domain.Auth.TokenRequest, observer: _root_.io.grpc.stub.StreamObserver[voz.auth.domain.Auth.TokenReply]): Unit =
          serviceImpl.getParentToken(request).onComplete(com.trueaccord.scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .addMethod(
      METHOD_GET_TOKEN,
      _root_.io.grpc.stub.ServerCalls.asyncUnaryCall(new _root_.io.grpc.stub.ServerCalls.UnaryMethod[voz.auth.domain.Auth.TokenRequest, voz.auth.domain.Auth.TokenReply] {
        override def invoke(request: voz.auth.domain.Auth.TokenRequest, observer: _root_.io.grpc.stub.StreamObserver[voz.auth.domain.Auth.TokenReply]): Unit =
          serviceImpl.getToken(request).onComplete(com.trueaccord.scalapb.grpc.Grpc.completeObserver(observer))(
            executionContext)
      }))
    .build()
  
  def blockingStub(channel: _root_.io.grpc.Channel): AuthBlockingStub = new AuthBlockingStub(channel)
  
  def stub(channel: _root_.io.grpc.Channel): AuthStub = new AuthStub(channel)
  
  def javaDescriptor: _root_.com.google.protobuf.Descriptors.ServiceDescriptor = voz.auth.domain.Auth.AuthProto.javaDescriptor.getServices().get(0)
  
}